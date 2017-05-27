package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

final class TurnEndMessage {
    private TurnEndMessage() {}

    static void handle(MessageSender sender, CountDownLatch turnEndLatch) throws IOException {
        sender.sendTelemetry();
        turnEndLatch.countDown();
    }
}
