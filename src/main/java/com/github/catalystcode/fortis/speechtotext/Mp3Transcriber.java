package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.utils.RiffHeader;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;

class Mp3Transcriber implements Transcriber {
    private static final int MP3_BUFFER_SIZE = 512 * 1024;
    private final static Logger log = Logger.getLogger(Mp3Transcriber.class);

    private final SpeechServiceConfig config;
    private final SpeechServiceClient client;

    Mp3Transcriber(SpeechServiceConfig config, SpeechServiceClient client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void transcribe(InputStream mp3Stream, Func<String> onResult, Func<String> onHypothesis) throws Exception {
        MessageReceiver receiver = new MessageReceiver(onResult, onHypothesis, client.getEndLatch());
        try {
            MessageSender sender = client.start(config, receiver);
            receiver.setSender(sender);
            sender.sendConfiguration();
            byte[] buf = new byte[MP3_BUFFER_SIZE];
            int read;
            while ((read = mp3Stream.read(buf)) != -1) {
                String mp3Path = newTempFile(".mp3");
                writeBytes(mp3Path, buf, read);
                sendAudioAsync(mp3Path, sender);
            }
            sender.sendAudioEnd();
            client.awaitEnd();
        } finally {
            client.stop();
        }
    }

    private static void convertAudio(String mp3Path, String wavPath) throws JavaLayerException {
        new Converter().convert(mp3Path, wavPath);
        log.debug("Converted " + mp3Path + " to " + wavPath);
    }

    private static void writeBytes(String path, byte[] buf, int length) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(path)) {
            outputStream.write(buf, 0, length);
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
                sender.sendAudio(wavStream, getSampleRate(wavStream));
            } catch (IOException ex) {
                log.error("Error sending audio", ex);
            } finally {
                deleteTempFile(mp3Path);
                deleteTempFile(wavPath);
            }
        }).run();
    }

    private static int getSampleRate(InputStream wavStream) throws IOException {
        RiffHeader header = RiffHeader.fromStream(wavStream);
        int sampleRate = header.sampleRate;
        log.debug("Got WAV stream with sample rate of " + sampleRate + "hz");
        return sampleRate;
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
