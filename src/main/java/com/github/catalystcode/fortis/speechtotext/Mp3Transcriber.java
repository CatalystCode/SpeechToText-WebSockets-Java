package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.catalystcode.fortis.speechtotext.utils.Environment.getMp3BufferSize;
import static java.nio.ByteBuffer.allocate;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;

class Mp3Transcriber implements Transcriber {
    private static final Logger log = Logger.getLogger(Mp3Transcriber.class);

    private final SpeechServiceConfig config;
    private final SpeechServiceClient client;
    private final int bufferSize;

    Mp3Transcriber(SpeechServiceConfig config, SpeechServiceClient client) {
        this.config = config;
        this.client = client;
        this.bufferSize = getMp3BufferSize();
    }

    @Override
    public void transcribe(InputStream mp3Stream, Func<String> onResult, Func<String> onHypothesis) throws Exception {
        MessageReceiver receiver = new MessageReceiver(onResult, onHypothesis, client.getEndLatch());
        try {
            MessageSender sender = client.start(config, receiver);
            receiver.setSender(sender);
            sender.sendConfiguration();
            byte[] streamBuf = new byte[bufferSize];
            ByteBuffer mp3Buf = allocate(bufferSize);
            int mp3BufPos = 0;
            int read;
            while ((read = mp3Stream.read(streamBuf)) != -1) {
                if (mp3BufPos + read >= bufferSize) {
                    log.info("Buffer full, starting to process " + mp3BufPos + " bytes");
                    String mp3Path = newTempFile(".mp3");
                    writeBytes(mp3Path, mp3Buf, mp3BufPos);
                    sendAudioAsync(mp3Path, sender);
                    mp3Buf.clear();
                    mp3Buf.put(streamBuf, 0, read);
                    mp3BufPos = read;
                } else {
                    mp3Buf.put(streamBuf, 0, read);
                    mp3BufPos += read;
                    log.debug("Buffered " + mp3BufPos + "/" + bufferSize + " bytes from MP3 stream");
                }
            }
            sender.sendAudioEnd();
            client.awaitEnd();
        } finally {
            client.stop();
        }
    }

    private static void convertAudio(String mp3Path, String wavPath) throws JavaLayerException {
        log.debug("Starting to convert " + mp3Path + " to " + wavPath);
        new Converter().convert(mp3Path, wavPath);
        log.debug("Converted " + mp3Path + " to " + wavPath);
    }

    private static void writeBytes(String path, ByteBuffer buf, int length) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            try (FileChannel channel = outputStream.getChannel()) {
                buf.flip();
                channel.write(buf);
            }
        }
        log.debug("Wrote " + length + " bytes to " + path);
    }

    private void sendAudioAsync(String mp3Path, MessageSender sender) {
        new Thread(() -> {
            String wavPath;
            try {
                wavPath = newTempFile(".wav");
            } catch (IOException ex) {
                log.error("Error creating temp file", ex);
                return;
            }

            try {
                convertAudio(mp3Path, wavPath);
            } catch (JavaLayerException ex) {
                log.error("Error converting MP3 to WAV", ex);
                return;
            }

            try (InputStream wavStream = new BufferedInputStream(new FileInputStream(wavPath))) {
                sender.sendAudio(wavStream);
            } catch (Exception ex) {
                log.error("Error sending audio", ex);
            } finally {
                deleteTempFile(mp3Path);
                deleteTempFile(wavPath);
            }
        }).run();
    }

    private String newTempFile(String suffix) throws IOException {
        return createTempFile(getClass().getName(), suffix).toString();
    }

    private static void deleteTempFile(String tempFile) {
        Path path = Paths.get(tempFile);
        try {
            deleteIfExists(path);
        } catch (IOException ex) {
            log.error("Error deleting temp file: " + tempFile, ex);
        }
    }
}
