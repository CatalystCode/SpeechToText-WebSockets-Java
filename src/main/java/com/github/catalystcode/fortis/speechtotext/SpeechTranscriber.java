package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.websocket.nv.NvSpeechServiceClient;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class SpeechTranscriber {
    private final SpeechServiceConfig config;
    private final CountDownLatch endLatch;

    public SpeechTranscriber(SpeechServiceConfig config) {
        this.config = config;
        this.endLatch = new CountDownLatch(1);
    }

    public void transcribe(InputStream wavStream, Func<String> onResult) throws Exception {
        SpeechServiceClient client = new NvSpeechServiceClient(endLatch);
        MessageReceiver receiver = new MessageReceiver(onResult, endLatch);

        try {
            MessageSender sender = client.start(config, receiver);
            receiver.setSender(sender);
            sender.sendConfiguration();
            sender.sendAudio(wavStream);
            client.awaitEnd();
        } finally {
            client.stop();
        }
    }
}
