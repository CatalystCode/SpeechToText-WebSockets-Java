package com.github.catalystcode.fortis.speechtotext.lifecycle;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

final class TurnEndMessage {
    private static final Logger log = Logger.getLogger(TurnEndMessage.class);
    private TurnEndMessage() {}

    static void handle(JSONObject message, CountDownLatch turnEndLatch) {
        // todo: send telemetry
        turnEndLatch.countDown();
    }
}
