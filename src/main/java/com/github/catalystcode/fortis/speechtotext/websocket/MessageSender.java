package com.github.catalystcode.fortis.speechtotext.websocket;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.createBinaryMessage;
import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.createTextMessage;
import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newGuid;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceContentTypes.JSON;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceContentTypes.WAV;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.AUDIO;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.SPEECH_CONFIG;

public abstract class MessageSender {
    private static final Logger log = Logger.getLogger(MessageSender.class);
    private static final int audioChunkSize = 8192;
    private final String requestId;

    protected MessageSender() {
        this.requestId = newGuid();
    }

    public final void sendConfiguration() throws IOException {
        String config = new PlatformInfo().toJson();
        String configMessage = createTextMessage(SPEECH_CONFIG, requestId, JSON, config);
        sendTextMessage(configMessage);
        log.info("Sent speech config: " + config);
    }

    public final void sendAudio(InputStream wavStream) throws IOException {
        byte[] buf = new byte[audioChunkSize];
        int chunksSent = 0;
        int read;
        while ((read = wavStream.read(buf)) != -1) {
            ByteBuffer audioChunkMessage = createBinaryMessage(AUDIO, requestId, WAV, buf, read);
            sendBinaryMessage(audioChunkMessage);
            chunksSent++;
            log.debug("Sent audio chunk " + chunksSent + "with " + read + " bytes");
        }
        log.info("Sent " + chunksSent + " audio chunks");
    }

    protected abstract void sendBinaryMessage(ByteBuffer message) throws IOException;
    protected abstract void sendTextMessage(String message) throws IOException;
}
