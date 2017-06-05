package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;

import java.io.InputStream;

class WavTranscriber extends Transcriber {
    WavTranscriber(SpeechServiceConfig config, SpeechServiceClient client) {
        super(config, client);
    }

    @Override
    protected void sendAudio(InputStream wavStream, MessageSender sender) {
        sendAudioAsync(wavStream, sender);
    }

    private void sendAudioAsync(InputStream wavStream, MessageSender sender) {
        new Thread(() -> {
            sender.sendAudio(wavStream);
            sender.sendAudioEnd();
        }).run();
    }
}
