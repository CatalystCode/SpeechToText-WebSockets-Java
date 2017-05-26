package com.github.catalystcode.fortis.speechtotext.config;

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
