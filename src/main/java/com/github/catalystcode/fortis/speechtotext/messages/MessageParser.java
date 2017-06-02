package com.github.catalystcode.fortis.speechtotext.messages;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.BODY_DELIM;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.HEADER_DELIM;

public final class MessageParser {
    private MessageParser() {}

    public static Map<String, String> parseHeaders(String message) {
        String[] parts = message.split(BODY_DELIM);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Message '" + message + "' does not have header and body");
        }
        String[] headerLines = parts[0].split(HEADER_DELIM);
        Map<String, String> headers = new HashMap<>(headerLines.length);
        for (String headerLine : headerLines) {
            String[] headerParts = headerLine.split(":");
            if (headerParts.length < 2) {
                throw new IllegalArgumentException("Header '" + headerLine + "' does not have a name and value");
            }
            String headerName = headerParts[0].trim();
            StringBuilder headerValueBuilder = new StringBuilder();
            for (int i = 1; i < headerParts.length; i++) {
                headerValueBuilder.append(headerParts[i]).append(':');
            }
            headerValueBuilder.setLength(headerValueBuilder.length() - 1);
            String headerValue = headerValueBuilder.toString().trim();
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    public static JSONObject parseBody(String message) {
        String[] parts = message.split(BODY_DELIM);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Message '" + message + "' does not have header and body");
        }
        String content = parts[1];
        return new JSONObject(content);
    }
}
