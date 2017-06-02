package com.github.catalystcode.fortis.speechtotext.messages;

import com.github.catalystcode.fortis.speechtotext.utils.RiffHeader;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.NUM_CHANNELS;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.SAMPLE_RATE;
import static com.github.catalystcode.fortis.speechtotext.messages.HeaderCreator.addHeaders;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.RIFF_HEADER_LENGHT;
import static com.github.catalystcode.fortis.speechtotext.utils.RiffHeader.putRiffHeader;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BinaryMessageCreator {
    private static final Logger log = Logger.getLogger(BinaryMessageCreator.class);

    private boolean isFirstMessage = true;
    private int sampleRate;

    public ByteBuffer createBinaryMessage(String path, String requestId, String contentType, byte[] wavBytes, int count) {
        RiffHeader riffHeader = isFirstMessage ? new RiffHeader(wavBytes) : null;
        if (isFirstMessage) setSampleRate(riffHeader);
        byte[] header = addHeaders(new StringBuilder(), path, requestId, contentType).toString().getBytes(UTF_8);
        int bufSize = 2 + header.length + count;
        if (isFirstMessage) bufSize += RIFF_HEADER_LENGHT;
        ByteBuffer buf = allocate(bufSize);
        buf.putShort((short)header.length);
        buf.put(header);
        int offset = isFirstMessage && riffHeader.isValid ? RIFF_HEADER_LENGHT : 0;
        int length = isFirstMessage && riffHeader.isValid ? count - RIFF_HEADER_LENGHT : count;
        if (isFirstMessage && count > 0) putRiffHeader(buf, SAMPLE_RATE, NUM_CHANNELS);
        if (count > 0) putAudio(buf, wavBytes, offset, length, sampleRate, SAMPLE_RATE);
        if (isFirstMessage) isFirstMessage = false;
        return buf;
    }

    private void setSampleRate(RiffHeader riffHeader) {
        if (riffHeader.isValid) {
            sampleRate = riffHeader.sampleRate;
        } else {
            log.warn("Got a WAV stream with invalid RIFF header, assuming " + SAMPLE_RATE + "hz sample rate");
            sampleRate = SAMPLE_RATE;
        }
    }

    private boolean needsResampling() {
        boolean needs = sampleRate != SAMPLE_RATE;
        if (needs) {
            throw new RuntimeException("foo");
        }
        return needs;
    }

    private void putAudio(ByteBuffer buf, byte[] wavBytes, int offset, int length, int actualSampleRate, int desiredSampleRate) {
        if (!needsResampling()) {
            buf.put(wavBytes, offset, length);
        }
    }
}
