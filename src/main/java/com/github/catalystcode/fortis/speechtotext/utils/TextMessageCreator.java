package com.github.catalystcode.fortis.speechtotext.utils;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.HEADER_DELIM;

public class TextMessageCreator extends MessageCreator {
    public String createTextMessage(String path, String requestId, String contentType, String message) {
        return addHeaders(new StringBuilder(), path, requestId, contentType).append(HEADER_DELIM).append(message).toString();
    }
}
