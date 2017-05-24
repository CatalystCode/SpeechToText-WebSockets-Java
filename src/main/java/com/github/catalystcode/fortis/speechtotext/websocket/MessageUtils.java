package com.github.catalystcode.fortis.speechtotext.websocket;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.github.catalystcode.fortis.speechtotext.websocket.ProtocolUtils.newTimestamp;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

final class MessageUtils {
    private MessageUtils() {}

    private static final String crlf = "\r\n";

    static Map<String, String> parseHeaders(String message) {
        String[] parts = message.split(crlf + crlf);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Message '" + message + "' does not have header and body");
        }
        String[] headerLines = parts[0].split(crlf);
        Map<String, String> headers = new HashMap<>(headerLines.length);
        for (String headerLine : headerLines) {
            String[] headerParts = headerLine.split(":");
            if (headerParts.length < 2) {
                throw new IllegalArgumentException("Header '" + headerLine + "' does not have a name and value");
            }
            String headerName = headerParts[0].trim();
            String headerValue = stream(headerParts).skip(1).collect(joining(":")).trim();
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    static JSONObject parseBody(String message) {
        String[] parts = message.split(crlf + crlf);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Message '" + message + "' does not have header and body");
        }
        String content = parts[1];
        return new JSONObject(content);
    }

    static ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int length) {
        byte[] header = addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(UTF_8);
        ByteBuffer buf = allocate(2 + header.length + length);
        buf.putShort(toShort(header.length));
        buf.put(header);
        if (length > 0) buf.put(wavBytes, 0, length);
        return buf;
    }

    static String createTextMessage(String path, String requestId, String contentType, String message) {
        return addHeaders(new StringBuilder(), path, requestId, contentType).append(crlf).append(message).toString();
    }

    private static StringBuilder addHeaders(StringBuilder sb, String path, String requestId, String contentType) {
        sb.append("Path: ").append(path).append(crlf);
        sb.append("X-RequestId: ").append(requestId).append(crlf);
        sb.append("X-Timestamp: ").append(newTimestamp()).append(crlf);
        sb.append("Content-Type: ").append(contentType).append(crlf);
        return sb;
    }

    private static short toShort(int num) {
        if (num > Short.MAX_VALUE) {
            throw new IllegalArgumentException(num + " > " + Short.MAX_VALUE);
        } else if (num < Short.MIN_VALUE) {
            throw new IllegalArgumentException(num + " < " + Short.MIN_VALUE);
        }

        return (short)num;
    }
}
