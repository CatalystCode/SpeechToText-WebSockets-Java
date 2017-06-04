package com.github.catalystcode.fortis.speechtotext.messages;

import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.NUM_CHANNELS;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.SAMPLE_RATE;
import static com.github.catalystcode.fortis.speechtotext.messages.HeaderCreator.addHeaders;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.RIFF_HEADER_LENGTH;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.putRiffHeader;
import static java.lang.Math.round;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BinaryMessageCreator {
    private boolean isFirstMessage = true;

    public ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int sampleRate, int count) {
        byte[] headers = formatHeaders(path, requestId, contentType);
        ByteBuffer buf = allocateBuffer(count, sampleRate, headers.length);
        putHeader(headers, buf);
        putContent(wavBytes, sampleRate, count, buf);
        updateState();
        return buf;
    }

    private static byte[] formatHeaders(String path, String requestId, String contentType) {
        return addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(UTF_8);
    }

    private void putContent(byte[] wavBytes, int sampleRate, int count, ByteBuffer buf) {
        if (count <= 0) {
            return;
        }

        int offset = isFirstMessage ? RIFF_HEADER_LENGTH : 0;
        int length = isFirstMessage ? count - RIFF_HEADER_LENGTH : count;
        if (isFirstMessage) putRiffHeader(buf, SAMPLE_RATE, NUM_CHANNELS);
        putAudio(buf, wavBytes, sampleRate, offset, length);
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

    private ByteBuffer allocateBuffer(int numWavBytes, int sampleRate, int numHeaderBytes) {
        int bufSize = 2 + numHeaderBytes;
        if (isFirstMessage) bufSize += RIFF_HEADER_LENGTH;
        if (sampleRate == SAMPLE_RATE) bufSize += numWavBytes;
        else bufSize += computeResampledNumBytes(numWavBytes, sampleRate);
        return allocate(bufSize);
    }

    private static int computeResampledNumBytes(int numWavBytes, int sampleRate) {
        return (int)round(numWavBytes / (sampleRate / (double)SAMPLE_RATE));
    }

    private static void putAudio(ByteBuffer buf, byte[] wavBytes, int sampleRate, int offset, int length) {
        if (sampleRate <= SAMPLE_RATE) {
            buf.put(wavBytes, offset, length);
            return;
        }

        double sampleRatio = sampleRate / (double)SAMPLE_RATE;
        int offsetBuffer = 0;
        int resampledNumBytes = computeResampledNumBytes(length, sampleRate);
        for (int offsetResult = 0; offsetResult < resampledNumBytes; offsetResult++) {
            int nextOffsetBuffer = (int)round((offsetResult + 1) * sampleRatio);
            int accum = 0;
            int count = 0;
            for (int i = offsetBuffer; i < nextOffsetBuffer && i < length; i++, count++) {
                accum += wavBytes[i + offset];
            }
            buf.put((byte)(accum / count));
            offsetBuffer = nextOffsetBuffer;
        }
    }
}
