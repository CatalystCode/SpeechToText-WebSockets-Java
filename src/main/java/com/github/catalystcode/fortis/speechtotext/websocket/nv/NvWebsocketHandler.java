package com.github.catalystcode.fortis.speechtotext.websocket.nv;

import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

class NvWebsocketHandler extends WebSocketAdapter {
    private static final Logger log = Logger.getLogger(NvWebsocketHandler.class);
    private final CountDownLatch socketCloseLatch;
    private final MessageReceiver receiver;

    NvWebsocketHandler(CountDownLatch socketCloseLatch, MessageReceiver receiver) {
        this.socketCloseLatch = socketCloseLatch;
        this.receiver = receiver;
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        log.debug("Websocket connected");
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        receiver.onMessage(text);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        log.error("Websocket read error", cause);
        socketCloseLatch.countDown();
    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        log.info("Websocket closed with status '" + frame.getCloseCode() + "' and reason '" + frame.getCloseReason() + "'");
        socketCloseLatch.countDown();
    }
}
