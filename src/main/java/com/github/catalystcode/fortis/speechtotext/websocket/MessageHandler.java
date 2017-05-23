package com.github.catalystcode.fortis.speechtotext.websocket;

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
public class MessageHandler {
    private static final Logger log = Logger.getLogger(MessageHandler.class);
    private static final int sessionTimeout = -1;
    private final CountDownLatch countDownLatch;
    private Session session;

    public MessageHandler(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        session.setIdleTimeout(sessionTimeout);
        this.session = session;
        log.info("Websocket connected");
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        log.debug("Websocket got message: %s" + message);
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        session.close();
        log.error("Websocket read error", error);
        countDownLatch.countDown();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        log.info("Websocket closed with status " + statusCode + " and reason " + reason);
        countDownLatch.countDown();
    }
}
