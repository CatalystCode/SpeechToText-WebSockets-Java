package com.github.catalystcode.fortis.speechtotext.websocket.nv;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.util.concurrent.CountDownLatch;

import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newGuid;

public class NvSpeechServiceClient implements SpeechServiceClient {
    private final CountDownLatch socketCloseLatch;
    private WebSocket webSocket;

    public NvSpeechServiceClient() {
        this.socketCloseLatch = new CountDownLatch(1);
    }

    @Override
    public MessageSender start(SpeechServiceConfig config, MessageReceiver receiver) throws Exception {
        String connectionId = newGuid();
        ConnectionTelemetry telemetry = ConnectionTelemetry.forId(connectionId);

        WebSocketFactory factory = new WebSocketFactory();
        webSocket = factory.createSocket(config.getConnectionUrl(connectionId));
        webSocket.addListener(new NvMessageReceiver(socketCloseLatch, receiver, telemetry));
        telemetry.recordConnectionStarted();
        webSocket.connect();
        return new NvMessageSender(connectionId, webSocket);
    }

    @Override
    public void stop() {
        webSocket.disconnect();
    }

    @Override
    public void awaitEnd() throws InterruptedException {
        socketCloseLatch.await();
    }

    @Override
    public CountDownLatch getEndLatch() {
        return socketCloseLatch;
    }
}
