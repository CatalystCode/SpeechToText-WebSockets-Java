package com.github.catalystcode.fortis.speechtotext.messages;

import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BinaryMessageCreator extends MessageCreator {
    public ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int length) {
        byte[] header = addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(UTF_8);
        ByteBuffer buf = allocate(2 + header.length + length);
        buf.putShort((short)header.length);
        buf.put(header);
        if (length > 0) buf.put(wavBytes, 0, length);
        return buf;
    }
}
