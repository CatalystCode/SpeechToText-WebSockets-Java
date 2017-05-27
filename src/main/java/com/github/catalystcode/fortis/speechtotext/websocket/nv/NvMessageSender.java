package com.github.catalystcode.fortis.speechtotext.websocket.nv;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.neovisionaries.ws.client.WebSocket;

import java.io.IOException;
import java.nio.ByteBuffer;

class NvMessageSender extends MessageSender {
    private final WebSocket webSocket;

    NvMessageSender(String connectionId, WebSocket webSocket) {
        super(connectionId);
        this.webSocket = webSocket;
    }

    @Override
    protected void sendBinaryMessage(ByteBuffer message) throws IOException {
        webSocket.sendBinary(message.array());
    }

    @Override
    protected void sendTextMessage(String message) throws IOException {
        webSocket.sendText(message);
    }
}
