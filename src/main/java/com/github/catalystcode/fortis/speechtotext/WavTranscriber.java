package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import org.apache.log4j.Logger;

import java.io.InputStream;

class WavTranscriber extends Transcriber {
    private static final Logger log = Logger.getLogger(WavTranscriber.class);

    WavTranscriber(SpeechServiceConfig config, SpeechServiceClient client) {
        super(config, client);
    }

    @Override
    protected void sendAudio(InputStream wavStream, MessageSender sender) {
        sendAudioAsync(wavStream, sender);
    }

    private void sendAudioAsync(InputStream wavStream, MessageSender sender) {
        new Thread(() -> {
            try {
                sender.sendAudio(wavStream);
                sender.sendAudioEnd();
            } catch (Exception ex) {
                log.error("Error sending audio", ex);
            }
        }).run();
    }
}
