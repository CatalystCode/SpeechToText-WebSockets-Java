package com.github.catalystcode.fortis.speechtotext.websocket;

import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.websocket.ProtocolUtils.newTimestamp;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.US_ASCII;

final class MessageUtils {
    private MessageUtils() {}

    private static final String crlf = "\r\n";

    static ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int length) {
        byte[] header = addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(US_ASCII);
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
