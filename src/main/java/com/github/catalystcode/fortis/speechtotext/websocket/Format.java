package com.github.catalystcode.fortis.speechtotext.websocket;

@SuppressWarnings("unused")
public enum Format {
    SIMPLE("simple"),
    DETAILED("detailed"),
    ;

    public final String value;

    Format(String value) {
        this.value = value;
    }
}
