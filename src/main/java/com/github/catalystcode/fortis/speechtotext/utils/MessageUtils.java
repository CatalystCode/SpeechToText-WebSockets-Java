package com.github.catalystcode.fortis.speechtotext.utils;

import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.*;
import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newTimestamp;
import static java.lang.Short.MAX_VALUE;
import static java.lang.Short.MIN_VALUE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class MessageUtils {
    private MessageUtils() {}

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
