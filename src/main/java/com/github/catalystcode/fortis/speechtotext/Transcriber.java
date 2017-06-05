package com.github.catalystcode.fortis.speechtotext;

import com.github.catalystcode.fortis.speechtotext.config.SpeechServiceConfig;
import com.github.catalystcode.fortis.speechtotext.utils.Func;

import java.io.InputStream;

public interface Transcriber {
    void transcribe(InputStream audioStream, Func<String> onResult, Func<String> onHypothesis) throws Exception;

    static Transcriber create(String audioPath, SpeechServiceConfig config) {
        if (audioPath.endsWith(".wav")) {
            return new WavTranscriber(config);
        }

        throw new IllegalArgumentException("Unsupported audio file type: " + audioPath);
    }
}
