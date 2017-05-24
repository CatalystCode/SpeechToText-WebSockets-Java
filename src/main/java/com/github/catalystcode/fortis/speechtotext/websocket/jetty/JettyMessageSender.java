package com.github.catalystcode.fortis.speechtotext.websocket.jetty;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;

import java.io.IOException;
import java.nio.ByteBuffer;

class JettyMessageSender extends MessageSender {
    private final RemoteEndpoint remote;

    JettyMessageSender(RemoteEndpoint remote) {
        this.remote = remote;
    }

    @Override
    protected void sendBinaryMessage(ByteBuffer message) throws IOException {
        remote.sendBytes(message);
    }

    @Override
    protected void sendTextMessage(String message) throws IOException {
        remote.sendString(message);
    }
}
