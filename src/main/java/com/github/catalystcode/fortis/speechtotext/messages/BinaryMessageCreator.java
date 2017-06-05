package com.github.catalystcode.fortis.speechtotext.messages;

import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.NUM_CHANNELS;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.SAMPLE_RATE;
import static com.github.catalystcode.fortis.speechtotext.messages.HeaderCreator.addHeaders;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.RIFF_HEADER_LENGTH;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.putRiffHeader;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BinaryMessageCreator {
    private boolean isFirstMessage;

    public BinaryMessageCreator() {
        this(true);
    }

    BinaryMessageCreator(boolean isFirstMessage) {
        this.isFirstMessage = isFirstMessage;
    }

    public ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int count) {
        byte[] headers = formatHeaders(path, requestId, contentType);
        ByteBuffer buf = allocateBuffer(count, headers.length);
        putHeader(headers, buf);
        putContent(wavBytes, count, buf);
        updateState();
        return buf;
    }

    private static byte[] formatHeaders(String path, String requestId, String contentType) {
        return addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(UTF_8);
    }

    private void putContent(byte[] wavBytes, int count, ByteBuffer buf) {
        if (count <= 0) {
            return;
        }

        int offset = isFirstMessage ? RIFF_HEADER_LENGTH : 0;
        int length = isFirstMessage ? count - RIFF_HEADER_LENGTH : count;
        if (isFirstMessage) putRiffHeader(buf, SAMPLE_RATE, NUM_CHANNELS);
        buf.put(wavBytes, offset, length);
    }

    private void updateState() {
        if (isFirstMessage) {
            isFirstMessage = false;
        }
    }

    private static void putHeader(byte[] header, ByteBuffer buf) {
        buf.putShort((short)header.length);
        buf.put(header);
    }

    private ByteBuffer allocateBuffer(int numWavBytes, int numHeaderBytes) {
        int bufSize = 2 + numHeaderBytes;
        if (isFirstMessage) bufSize += RIFF_HEADER_LENGTH;
        bufSize += numWavBytes;
        return allocate(bufSize);
    }
}
