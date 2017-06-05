package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.websocket.SpeechServiceClient;
import com.github.catalystcode.fortis.speechtotext.websocket.nv.NvSpeechServiceClient;

import java.io.InputStream;

public interface Transcriber {
    void transcribe(InputStream audioStream, Func<String> onResult, Func<String> onHypothesis) throws Exception;

    static Transcriber create(String audioPath, SpeechServiceConfig config) {
        return create(audioPath, config, new NvSpeechServiceClient());
    }

    static Transcriber create(String audioPath, SpeechServiceConfig config, SpeechServiceClient client) {
        if (audioPath.endsWith(".wav")) {
            return new WavTranscriber(config, client);
        }

        if (audioPath.endsWith(".mp3")) {
            return new Mp3Transcriber(config, client);
        }

        throw new IllegalArgumentException("Unsupported audio file type: " + audioPath);
    }
}
