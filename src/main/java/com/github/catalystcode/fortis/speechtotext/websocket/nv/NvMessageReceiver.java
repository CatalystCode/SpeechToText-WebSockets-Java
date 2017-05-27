package com.github.catalystcode.fortis.speechtotext.websocket.nv;

import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceWebsocketStatusCodes.OK;

class NvMessageReceiver extends WebSocketAdapter {
    private static final Logger log = Logger.getLogger(NvMessageReceiver.class);
    private final CountDownLatch socketCloseLatch;
    private final MessageReceiver receiver;
    private final ConnectionTelemetry telemetry;

    NvMessageReceiver(CountDownLatch socketCloseLatch, MessageReceiver receiver, ConnectionTelemetry telemetry) {
        this.socketCloseLatch = socketCloseLatch;
        this.receiver = receiver;
        this.telemetry = telemetry;
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        telemetry.recordConnectionEstablished();
        log.debug("Websocket connected");
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        receiver.onMessage(text);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        telemetry.recordConnectionFailed(cause.getMessage());
        log.error("Websocket read error", cause);
        socketCloseLatch.countDown();
    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        int closeCode = frame.getCloseCode();
        String closeReason = frame.getCloseReason();

        if (closeCode != OK) {
            telemetry.recordConnectionFailed(closeReason);
        }

        log.info("Websocket closed with status '" + closeCode + "' and reason '" + closeReason + "'");
        socketCloseLatch.countDown();
    }
}
