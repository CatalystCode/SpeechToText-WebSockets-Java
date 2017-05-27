package com.github.catalystcode.fortis.speechtotext.websocket;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceContentTypes.JSON;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceContentTypes.WAV;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.MAX_BYTES_PER_AUDIO_CHUNK;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.*;
import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.createBinaryMessage;
import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.createTextMessage;
import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newGuid;

public abstract class MessageSender {
    private static final Logger log = Logger.getLogger(MessageSender.class);

    private final String connectionId;
    private final String requestId;

    protected MessageSender(String connectionId, String requestId) {
        this.connectionId = connectionId;
        this.requestId = requestId;
    }

    public final void sendConfiguration() throws IOException {
        String config = new PlatformInfo().toJson();
        String configMessage = createTextMessage(SPEECH_CONFIG, requestId, JSON, config);
        sendTextMessage(configMessage);
        log.info("Sent speech config: " + config);
    }

    public final void sendAudio(InputStream wavStream) throws IOException {
        byte[] buf = new byte[MAX_BYTES_PER_AUDIO_CHUNK];
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

    public final void sendTelemetry() throws IOException {
        String telemetry = new TelemetryInfo(connectionId, requestId).toJson();
        String telemetryMessage = createTextMessage(TELEMETRY, requestId, JSON, telemetry);
        sendTextMessage(telemetryMessage);
        log.info("Sent telemetry: " + telemetry);
    }

    protected abstract void sendBinaryMessage(ByteBuffer message) throws IOException;
    protected abstract void sendTextMessage(String message) throws IOException;
}
