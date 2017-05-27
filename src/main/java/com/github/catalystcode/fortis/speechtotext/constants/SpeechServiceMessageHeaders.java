package com.github.catalystcode.fortis.speechtotext.constants;

public final class SpeechServiceMessageHeaders {
    private SpeechServiceMessageHeaders() {}

    public static final String PATH = "Path";
    public static final String REQUEST_ID = "X-RequestId";
    public static final String TIMESTAMP = "X-Timestamp";
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String HEADER_DELIM = "\r\n";
    public static final String BODY_DELIM = "\r\n\r\n";
}
