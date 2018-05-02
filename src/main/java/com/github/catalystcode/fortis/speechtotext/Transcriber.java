package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.websocket.nv.NvSpeechServiceClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public abstract class Transcriber {
    protected final SpeechServiceConfig config;
    private final SpeechServiceClient client;

    Transcriber(SpeechServiceConfig config, SpeechServiceClient client) {
        this.config = config;
        this.client = client;
    }

    public void transcribe(InputStream audioStream, Consumer<String> onResult, Consumer<String> onHypothesis)
            throws Exception {
        transcribe(audioStream, onResult, onHypothesis, x -> {
        });
    }

    public void transcribe(InputStream audioStream, Consumer<String> onResult, Consumer<String> onHypothesis,
            Consumer<Exception> onError) throws Exception {
        MessageReceiver receiver = new MessageReceiver(onResult, onHypothesis, onError, client.getEndLatch());
        try {
            MessageSender sender = client.start(config, receiver, onError);
            receiver.setSender(sender);
            sender.sendConfiguration();
            sendAudio(audioStream, sender);
            client.awaitEnd();
        } finally {
            client.stop();
        }
    }

    protected abstract void sendAudio(InputStream audioStream, MessageSender sender) throws IOException;

    public static Transcriber create(String audioPath, SpeechServiceConfig config) {
        return create(audioPath, config, new NvSpeechServiceClient());
    }

    public static Transcriber create(SpeechServiceConfig config) {
        return create(config, new NvSpeechServiceClient());
    }

    private static Transcriber create(String audioPath, SpeechServiceConfig config, SpeechServiceClient client) {
        if (audioPath.endsWith(".wav")) {
            return new WavTranscriber(config, client);
        }

        if (audioPath.endsWith(".mp3")) {
            return new Mp3Transcriber(config, client);
        }

        throw new IllegalArgumentException("Unsupported audio file type: " + audioPath);
    }

    private static Transcriber create(SpeechServiceConfig config, SpeechServiceClient client) {
        return new WavTranscriber(config, client);
    }
}
