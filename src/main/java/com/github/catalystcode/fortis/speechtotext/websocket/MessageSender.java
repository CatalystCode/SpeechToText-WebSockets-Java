package com.github.catalystcode.fortis.speechtotext.websocket;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.createBinaryMessage;
import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.createTextMessage;
import static com.github.catalystcode.fortis.speechtotext.websocket.ProtocolUtils.newGuid;

public abstract class MessageSender {
    private static final Logger log = Logger.getLogger(MessageSender.class);
    private static final int audioChunkSize = 8192;
    private final String requestId;

    protected MessageSender() {
        this.requestId = newGuid();
    }

    public final void sendConfiguration() throws IOException {
        String configMessage = createTextMessage(
          "speech.config",
          requestId,
          "application/json; charset=utf-8",
          getConfig());

        sendTextMessage(configMessage);
    }

    public final void sendAudio(InputStream wavStream) throws IOException {
        byte[] buf = new byte[audioChunkSize];
        int chunksSent = 0;
        int read;
        while ((read = wavStream.read(buf)) != -1) {
            ByteBuffer audioChunkMessage = createBinaryMessage("audio", requestId, "audio/wav", buf, read);
            sendBinaryMessage(audioChunkMessage);
            chunksSent++;
            log.debug("Sent audio chunk " + chunksSent + "with " + read + " bytes");
        }
        log.info("Sent " + chunksSent + " audio chunks");
    }

    private String getConfig() {
        return
            "{" +
            " \"context\": {" +
            "  \"system\": {" +
            "   \"version\": \"0.0.1\"" +
            "  }" +
            " }," +
            " \"os\": {" +
            "  \"platform\": \"Windows\"," +
            "  \"name\": \"Windows 10\"," +
            "  \"version\": \"15063.296\"" +
            " }," +
            " \"device\": {" +
            "  \"manufacturer\": \"SpeechSample\"," +
            "  \"model\": \"SpeechSample\"," +
            "  \"version\": \"1.0.0000\"" +
            " }" +
            "}";
    }

    protected abstract void sendBinaryMessage(ByteBuffer message) throws IOException;
    protected abstract void sendTextMessage(String message) throws IOException;
}
