package com.github.catalystcode.fortis.speechtotext.websocket;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.createBinaryMessage;
import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.createTextMessage;
import static com.github.catalystcode.fortis.speechtotext.websocket.ProtocolUtils.newGuid;
import static java.lang.Math.min;

public class MessageSender {
    private static final Logger log = Logger.getLogger(MessageSender.class);
    private static final int audioChunkSize = 8192;
    private final RemoteEndpoint remote;
    private final String requestId;

    public MessageSender(RemoteEndpoint remote) {
        this.remote = remote;
        this.requestId = newGuid();
    }

    public void sendConfiguration() throws IOException {
        String configMessage = createTextMessage(
          "speech.config",
          requestId,
          "application/json; charset=utf-8",
          getConfig());

        remote.sendString(configMessage);
    }

    public void sendAudio(byte[] wavBytes) throws IOException {
        for (int offset = 0; offset < wavBytes.length; offset += audioChunkSize) {
            int length = min(audioChunkSize, wavBytes.length - offset);
            ByteBuffer audioChunkMessage = createBinaryMessage("audio", requestId, "audio/wav", wavBytes, offset, length);
            remote.sendBytes(audioChunkMessage);
            log.debug("Sent audio chunk of " + length + " bytes starting at " + offset + " (" + (wavBytes.length - offset - length) + " left)");
        }

        ByteBuffer audioEndMessage = createBinaryMessage("audio", requestId, "audio/wav", new byte[0], 0, 0);
        remote.sendBytes(audioEndMessage);
        log.debug("Sent end-of-audio marker");
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
}
