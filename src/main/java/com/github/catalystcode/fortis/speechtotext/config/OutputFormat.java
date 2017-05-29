package com.github.catalystcode.fortis.speechtotext.config;

@SuppressWarnings("unused")
public enum OutputFormat {
    SIMPLE("simple"),
    DETAILED("detailed"),
    ;

    public final String value;

    OutputFormat(String value) {
        this.value = value;
    }
}
