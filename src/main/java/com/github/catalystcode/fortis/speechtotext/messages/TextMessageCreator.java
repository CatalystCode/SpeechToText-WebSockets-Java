package com.github.catalystcode.fortis.speechtotext.messages;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.HEADER_DELIM;
import static com.github.catalystcode.fortis.speechtotext.messages.HeaderCreator.addHeaders;

public final class TextMessageCreator {
    private TextMessageCreator() {}

    public static String createTextMessage(String path, String requestId, String contentType, String message) {
        return addHeaders(new StringBuilder(), path, requestId, contentType).append(HEADER_DELIM).append(message).toString();
    }
}
