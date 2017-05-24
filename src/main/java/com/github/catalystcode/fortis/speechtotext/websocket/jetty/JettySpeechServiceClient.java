package com.github.catalystcode.fortis.speechtotext.websocket.jetty;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceConfig;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public class JettySpeechServiceClient implements SpeechServiceClient {
    private final CountDownLatch socketCloseLatch;
    private WebSocketClient client;
    private Session session;

    public JettySpeechServiceClient() {
        this.socketCloseLatch = new CountDownLatch(1);
    }

    @Override
    public MessageSender start(SpeechServiceConfig config, MessageReceiver receiver) throws Exception {
        client = new WebSocketClient(new SslContextFactory());
        client.start();
        JettyWebsocketHandler handler = new JettyWebsocketHandler(socketCloseLatch, receiver);
        Future<Session> future = client.connect(handler, URI.create(config.getConnectionUrl()), new ClientUpgradeRequest());
        session = future.get();
        return new JettyMessageSender(session.getRemote());
    }

    @Override
    public void stop() throws Exception {
        if (session != null) {
            session.close();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Override
    public void awaitEnd() throws InterruptedException {
        socketCloseLatch.await();
    }
}
