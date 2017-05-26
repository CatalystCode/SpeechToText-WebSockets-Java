package com.github.catalystcode.fortis.speechtotext.websocket.nv;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.util.concurrent.CountDownLatch;

public class NvSpeechServiceClient implements SpeechServiceClient {
    private final CountDownLatch socketCloseLatch;
    private WebSocket webSocket;

    public NvSpeechServiceClient() {
        this.socketCloseLatch = new CountDownLatch(1);
    }

    @Override
    public MessageSender start(SpeechServiceConfig config, MessageReceiver receiver) throws Exception {
        WebSocketFactory factory = new WebSocketFactory();
        webSocket = factory.createSocket(config.getConnectionUrl());
        webSocket.addListener(new NvWebsocketHandler(socketCloseLatch, receiver));
        webSocket.connect();
        return new NvMessageSender(webSocket);
    }

    @Override
    public void stop() throws Exception {
        webSocket.disconnect();
    }

    @Override
    public void awaitEnd() throws InterruptedException {
        socketCloseLatch.await();
    }
}
