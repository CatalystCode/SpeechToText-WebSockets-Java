package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.websocket.nv.NvSpeechServiceClient;

import javax.sound.sampled.AudioFormat;
import java.io.InputStream;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class SpeechTranscriber {
    private static final AudioFormat SPEECH_SERVICE_AUDIO_FORMAT = new AudioFormat(16000, 16, 1, false, false);
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

    public void transcribe(InputStream wavStream, Func<String> onResult) throws Exception {
        transcribe(wavStream, onResult, null);
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    public void transcribe(InputStream wavStream, Func<String> onResult, Func<String> onHypothesis) throws Exception {
        try (InputStream pcmStream = getAudioInputStream(SPEECH_SERVICE_AUDIO_FORMAT, getAudioInputStream(wavStream))) {
            MessageReceiver receiver = new MessageReceiver(onResult, onHypothesis, client.getEndLatch());
            try {
                MessageSender sender = client.start(config, receiver);
                receiver.setSender(sender);
                sender.sendConfiguration();
                sendAudioAsync(pcmStream, sender);
                client.awaitEnd();
            } finally {
                client.stop();
            }
        }
    }

    private void sendAudioAsync(InputStream pcmStream, MessageSender sender) {
        new Thread(() -> sender.sendAudio(pcmStream)).run();
    }
}
