package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.utils.RiffHeader;
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

    @SuppressWarnings("WeakerAccess")
    public SpeechTranscriber(SpeechServiceConfig config, SpeechServiceClient client) {
        this.config = config;
        this.client = client;
    }

    public void transcribe(InputStream wavStream, Func<String> onResult, Func<String> onHypothesis) throws Exception {
        RiffHeader header = RiffHeader.fromStream(wavStream);
        transcribe(wavStream, header.sampleRate, onResult, onHypothesis);
    }

    @SuppressWarnings("WeakerAccess")
    public void transcribe(InputStream wavStream, int sampleRate, Func<String> onResult, Func<String> onHypothesis) throws Exception {
        MessageReceiver receiver = new MessageReceiver(onResult, onHypothesis, client.getEndLatch());
        try {
            MessageSender sender = client.start(config, receiver);
            receiver.setSender(sender);
            sender.sendConfiguration();
            sendAudioAsync(wavStream, sampleRate, sender);
            client.awaitEnd();
        } finally {
            client.stop();
        }
    }

    private void sendAudioAsync(InputStream wavStream, int sampleRate, MessageSender sender) {
        new Thread(() -> sender.sendAudio(wavStream, sampleRate)).run();
    }
}
