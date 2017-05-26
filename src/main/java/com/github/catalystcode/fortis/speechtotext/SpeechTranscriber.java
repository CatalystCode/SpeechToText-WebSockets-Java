package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.websocket.nv.NvSpeechServiceClient;

import java.io.InputStream;

public class SpeechTranscriber {
    private final SpeechServiceConfig config;
    private final SpeechServiceClient client;

    public SpeechTranscriber(SpeechServiceConfig config) {
        this(config, new NvSpeechServiceClient());
    }

    private SpeechTranscriber(SpeechServiceConfig config, SpeechServiceClient client) {
        this.config = config;
        this.client = client;
    }

    public void transcribe(InputStream wavStream, Func<String> onResult) throws Exception {
        MessageReceiver receiver = new MessageReceiver(onResult);

        try {
            MessageSender sender = client.start(config, receiver);
            sender.sendConfiguration();
            sender.sendAudio(wavStream);
            client.awaitEnd();
        } finally {
            client.stop();
        }
    }
}
