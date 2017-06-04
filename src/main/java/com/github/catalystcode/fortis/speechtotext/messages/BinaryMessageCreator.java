package com.github.catalystcode.fortis.speechtotext.messages;

import com.github.catalystcode.fortis.speechtotext.utils.RiffHeader;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.NUM_CHANNELS;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.SAMPLE_RATE;
import static com.github.catalystcode.fortis.speechtotext.messages.HeaderCreator.addHeaders;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.RIFF_HEADER_LENGHT;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.putRiffHeader;
import static java.lang.Math.round;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BinaryMessageCreator {
    private static final Logger log = Logger.getLogger(BinaryMessageCreator.class);

    private boolean isFirstMessage = true;
    private int sampleRate;

    public ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int count) {
        setSampleRate(wavBytes);
        byte[] headers = formatHeaders(path, requestId, contentType);
        ByteBuffer buf = allocateBuffer(count, headers.length);
        putHeader(headers, buf);
        putContent(wavBytes, count, buf);
        updateState();
        return buf;
    }

    private byte[] formatHeaders(String path, String requestId, String contentType) {
        return addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(UTF_8);
    }

    private void putContent(byte[] wavBytes, int count, ByteBuffer buf) {
        if (count <= 0) {
            return;
        }

        int offset = isFirstMessage ? RIFF_HEADER_LENGHT : 0;
        int length = isFirstMessage ? count - RIFF_HEADER_LENGHT : count;
        if (isFirstMessage) putRiffHeader(buf, SAMPLE_RATE, NUM_CHANNELS);
        putAudio(buf, wavBytes, offset, length);
    }

    private void updateState() {
        if (isFirstMessage) {
            isFirstMessage = false;
        }
    }

    private void putHeader(byte[] header, ByteBuffer buf) {
        buf.putShort((short)header.length);
        buf.put(header);
    }

    private ByteBuffer allocateBuffer(int numWavBytes, int numHeaderBytes) {
        int bufSize = 2 + numHeaderBytes;
        if (isFirstMessage) bufSize += RIFF_HEADER_LENGHT;
        if (!needsResampling()) bufSize += numWavBytes;
        else bufSize += computeResampledNumBytes(numWavBytes);
        return allocate(bufSize);
    }

    private int computeResampledNumBytes(int numWavBytes) {
        return (int)round(numWavBytes / (sampleRate / (double)SAMPLE_RATE));
    }

    private void setSampleRate(byte[] wavBytes) {
        if (!isFirstMessage) {
            return;
        }

        sampleRate = new RiffHeader(wavBytes).sampleRate;
        log.debug("Got WAV with sample rate of " + sampleRate + "hz");
    }

    private boolean needsResampling() {
        return sampleRate != SAMPLE_RATE;
    }

    private void putAudio(ByteBuffer buf, byte[] wavBytes, int offset, int length) {
        if (!needsResampling()) {
            buf.put(wavBytes, offset, length);
            return;
        }

        double sampleRatio = sampleRate / (double)SAMPLE_RATE;
        int offsetBuffer = 0;
        int resampledNumBytes = computeResampledNumBytes(length);
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
