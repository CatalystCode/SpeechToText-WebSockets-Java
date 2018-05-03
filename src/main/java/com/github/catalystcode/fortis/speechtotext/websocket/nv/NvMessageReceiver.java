package com.github.catalystcode.fortis.speechtotext.websocket.nv;

import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.apache.log4j.Logger;

import java.awt.IllegalComponentStateException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

class NvMessageReceiver extends WebSocketAdapter {
    private static final Logger log = Logger.getLogger(NvMessageReceiver.class);
    private final CountDownLatch socketCloseLatch;
    private final MessageReceiver receiver;
    private final ConnectionTelemetry telemetry;
    private final Consumer<Exception> onErrorCallback;

    NvMessageReceiver(CountDownLatch socketCloseLatch, MessageReceiver receiver, ConnectionTelemetry telemetry,
            Consumer<Exception> onError) {
        this.socketCloseLatch = socketCloseLatch;
        this.receiver = receiver;
        this.telemetry = telemetry;
        this.onErrorCallback = onError;
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        telemetry.recordConnectionEstablished();
        log.debug("Websocket connected");
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        telemetry.recordConnectionFailed(exception.getMessage());
        log.error("Websocket connection failed", exception);
        onErrorCallback.accept(exception);
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        receiver.onMessage(text);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        log.error("Websocket read error", cause);
        socketCloseLatch.countDown();
        onErrorCallback.accept(cause);
    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        int closeCode = frame.getCloseCode();
        String closeReason = frame.getCloseReason();

        log.info("Websocket closed with status '" + closeCode + "' and reason '" + closeReason + "'");
        socketCloseLatch.countDown();
        if (closeCode != WebSocketCloseCode.NORMAL) {
            onErrorCallback.accept(new IllegalComponentStateException(
                    "Websocket closed with status '" + closeCode + "' and reason '" + closeReason + "'"));
        }
    }
}
