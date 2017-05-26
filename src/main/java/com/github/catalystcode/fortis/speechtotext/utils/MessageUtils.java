package com.github.catalystcode.fortis.speechtotext.utils;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.*;
import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newTimestamp;
import static java.lang.Short.MAX_VALUE;
import static java.lang.Short.MIN_VALUE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class MessageUtils {
    private MessageUtils() {}

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

    public static ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int length) {
        byte[] header = addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(UTF_8);
        ByteBuffer buf = allocate(2 + header.length + length);
        buf.putShort(toShort(header.length));
        buf.put(header);
        if (length > 0) buf.put(wavBytes, 0, length);
        return buf;
    }

    public static String createTextMessage(String path, String requestId, String contentType, String message) {
        return addHeaders(new StringBuilder(), path, requestId, contentType).append(HEADER_DELIM).append(message).toString();
    }

    private static StringBuilder addHeaders(StringBuilder sb, String path, String requestId, String contentType) {
        sb.append(PATH).append(": ").append(path).append(HEADER_DELIM);
        sb.append(REQUEST_ID).append(": ").append(requestId).append(HEADER_DELIM);
        sb.append(TIMESTAMP).append(": ").append(newTimestamp()).append(HEADER_DELIM);
        sb.append(CONTENT_TYPE).append(": ").append(contentType).append(HEADER_DELIM);
        return sb;
    }

    private static short toShort(int num) {
        if (num > MAX_VALUE) {
            throw new IllegalArgumentException(num + " > " + MAX_VALUE);
        } else if (num < MIN_VALUE) {
            throw new IllegalArgumentException(num + " < " + MIN_VALUE);
        }

        return (short)num;
    }
}
