package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;

import java.util.concurrent.CountDownLatch;

public interface SpeechServiceClient {
    MessageSender start(SpeechServiceConfig config, MessageReceiver receiver) throws Exception;
    void stop();
    void awaitEnd() throws InterruptedException;
    CountDownLatch getEndLatch();
}
