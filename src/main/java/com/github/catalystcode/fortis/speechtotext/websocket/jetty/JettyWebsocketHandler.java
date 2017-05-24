package com.github.catalystcode.fortis.speechtotext.websocket.jetty;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageReceiver;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.CountDownLatch;

@SuppressWarnings("unused")
@WebSocket
public class JettyWebsocketHandler {
    private static final Logger log = Logger.getLogger(JettyWebsocketHandler.class);
    private final CountDownLatch socketCloseLatch;
    private final MessageReceiver receiver;

    JettyWebsocketHandler(CountDownLatch socketCloseLatch, MessageReceiver receiver) {
        this.socketCloseLatch = socketCloseLatch;
        this.receiver = receiver;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.debug("Websocket connected");
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        receiver.onMessage(message);
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        log.error("Websocket read error", error);
        socketCloseLatch.countDown();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        log.info("Websocket closed with status '" + statusCode + "' and reason '" + reason + "'");
        socketCloseLatch.countDown();
    }
}
