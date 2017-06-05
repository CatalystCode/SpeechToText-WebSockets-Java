package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.lifecycle.MessageReceiver;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import org.apache.log4j.Logger;

import java.io.InputStream;

class WavTranscriber implements Transcriber {
    private static final Logger log = Logger.getLogger(WavTranscriber.class);

    private final SpeechServiceConfig config;
    private final SpeechServiceClient client;

    WavTranscriber(SpeechServiceConfig config, SpeechServiceClient client) {
        this.config = config;
        this.client = client;
    }

    public void transcribe(InputStream wavStream, Func<String> onResult, Func<String> onHypothesis) throws Exception {
        MessageReceiver receiver = new MessageReceiver(onResult, onHypothesis, client.getEndLatch());
        try {
            MessageSender sender = client.start(config, receiver);
            receiver.setSender(sender);
            sender.sendConfiguration();
            sendAudioAsync(wavStream, sender);
            client.awaitEnd();
        } finally {
            client.stop();
        }
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
