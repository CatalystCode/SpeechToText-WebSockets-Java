package com.github.catalystcode.fortis.speechtotext.config;

@SuppressWarnings("unused")
public enum SpeechType {
    INTERACTIVE("/speech/recognition/interactive/cognitiveservices/v1"),
    DICTATION("/speech/recognition/dictation/cognitiveservices/v1"),
    CONVERSATION("/speech/recognition/conversation/cognitiveservices/v1"),
    ;

    public final String endpoint;

    SpeechType(String endpoint) {
        this.endpoint = endpoint;
    }
}
