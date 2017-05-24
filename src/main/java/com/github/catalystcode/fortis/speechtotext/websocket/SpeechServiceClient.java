package com.github.catalystcode.fortis.speechtotext.websocket;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.util.concurrent.Future;

public class SpeechServiceClient {
    private final SpeechServiceUrl url;
    private final MessageHandler handler;
    private WebSocketClient client;

    public SpeechServiceClient(SpeechServiceUrl url, MessageHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    public Future<Session> start() throws Exception {
        client = new WebSocketClient(new SslContextFactory());
        client.start();
        return client.connect(handler, url.toURI(), new ClientUpgradeRequest());
    }

    public void stop() throws Exception {
        client.stop();
    }
}
