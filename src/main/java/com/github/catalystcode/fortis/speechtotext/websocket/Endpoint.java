package com.github.catalystcode.fortis.speechtotext.websocket;

@SuppressWarnings("unused")
public enum Endpoint {
    INTERACTIVE("/speech/recognition/interactive/cognitiveservices/v1"),
    DICTATION("/speech/recognition/dictation/cognitiveservices/v1"),
    CONVERSATION("/speech/recognition/conversation/cognitiveservices/v1"),
    ;

    public final String path;

    Endpoint(String url) {
        this.path = url;
    }
}
