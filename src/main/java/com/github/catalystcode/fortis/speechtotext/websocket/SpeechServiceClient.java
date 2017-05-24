package com.github.catalystcode.fortis.speechtotext.websocket;

public interface SpeechServiceClient {
    MessageSender start(SpeechServiceConfig config, MessageReceiver receiver) throws Exception;
    void stop() throws Exception;
    void awaitEnd() throws InterruptedException;
}
