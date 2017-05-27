package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;

import java.util.concurrent.CountDownLatch;

final class TurnEndMessage {
    private TurnEndMessage() {}

    static void handle(MessageSender sender, CountDownLatch turnEndLatch) {
        sender.sendTelemetry();
        turnEndLatch.countDown();
    }
}
