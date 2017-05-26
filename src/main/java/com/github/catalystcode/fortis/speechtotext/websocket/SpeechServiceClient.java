package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;

public interface SpeechServiceClient {
    MessageSender start(SpeechServiceConfig config, MessageReceiver receiver) throws Exception;
    void stop() throws Exception;
    void awaitEnd() throws InterruptedException;
}
