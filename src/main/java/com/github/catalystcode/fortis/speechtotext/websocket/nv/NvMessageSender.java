package com.github.catalystcode.fortis.speechtotext.websocket.nv;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.neovisionaries.ws.client.WebSocket;

import java.nio.ByteBuffer;

class NvMessageSender extends MessageSender {
    private final WebSocket webSocket;

    NvMessageSender(String connectionId, WebSocket webSocket) {
        super(connectionId);
        this.webSocket = webSocket;
    }

    @Override
    protected void sendBinaryMessage(ByteBuffer message) {
        webSocket.sendBinary(message.array());
    }

    @Override
    protected void sendTextMessage(String message) {
        webSocket.sendText(message);
    }
}
