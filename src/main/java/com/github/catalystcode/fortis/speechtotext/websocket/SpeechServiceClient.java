package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public interface SpeechServiceClient {
    MessageSender start(SpeechServiceConfig config, MessageReceiver receiver, Consumer<Exception> onError)
            throws Exception;

    void stop();

    void awaitEnd() throws InterruptedException;

    CountDownLatch getEndLatch();
}
