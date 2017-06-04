package com.github.catalystcode.fortis.speechtotext.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.wrap;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class RiffHeader {
    public static final int RIFF_HEADER_LENGTH = 44;
    private static final int FORMAT_WAVE = 0x57415645;
    private static final int CHUNKID_RIFF = 0x52494646;
    private static final int SUBCHUNK1ID_FMT = 0x666d7420;
    private static final int SUBCHUNK2ID_DATA = 0x64617461;
    private static final short AUDIO_FORMAT_PCM = 1;

    public final int chunkId;
    public final int chunkSize;
    public final int format;
    public final int subChunk1ID;
    public final int subChunk1Size;
    public final short audioFormat;
    public final short numChannels;
    public final int sampleRate;
    public final int byteRate;
    public final short blockAlign;
    public final short bitsPerSample;
    public final int subChunk2Id;
    public final int subChunk2Size;

    public RiffHeader(byte[] wavBytes) {
        ByteBuffer waveHeader = wrap(wavBytes, 0, RIFF_HEADER_LENGTH);

        waveHeader.order(BIG_ENDIAN);
        chunkId = waveHeader.getInt();

        waveHeader.order(LITTLE_ENDIAN);
        chunkSize = waveHeader.getInt();

        waveHeader.order(BIG_ENDIAN);
        format = waveHeader.getInt();
        subChunk1ID = waveHeader.getInt();

        waveHeader.order(LITTLE_ENDIAN);
        subChunk1Size = waveHeader.getInt();
        audioFormat = waveHeader.getShort();
        numChannels = waveHeader.getShort();
        sampleRate = waveHeader.getInt();
        byteRate = waveHeader.getInt();
        blockAlign = waveHeader.getShort();
        bitsPerSample = waveHeader.getShort();

        waveHeader.order(BIG_ENDIAN);
        subChunk2Id = waveHeader.getInt();

        waveHeader.order(LITTLE_ENDIAN);
        subChunk2Size = waveHeader.getInt();
    }

    public static void putRiffHeader(ByteBuffer buf, int sampleRate, short numChannels) {
        int chunkSize = 0;
        int subChunk1Size = 16;
        int subChunk2Size = 0;
        short bitsPerSample = 16;
        int bytesPerSample = bitsPerSample / 8;
        int byteRate = sampleRate * numChannels * bytesPerSample;
        short blockAlign = (short)(numChannels * bytesPerSample);

        buf.order(BIG_ENDIAN);
        buf.putInt(CHUNKID_RIFF);
        buf.order(LITTLE_ENDIAN);
        buf.putInt(chunkSize);
        buf.order(BIG_ENDIAN);
        buf.putInt(FORMAT_WAVE);
        buf.putInt(SUBCHUNK1ID_FMT);
        buf.order(LITTLE_ENDIAN);
        buf.putInt(subChunk1Size);
        buf.putShort(AUDIO_FORMAT_PCM);
        buf.putShort(numChannels);
        buf.putInt(sampleRate);
        buf.putInt(byteRate);
        buf.putShort(blockAlign);
        buf.putShort(bitsPerSample);
        buf.order(BIG_ENDIAN);
        buf.putInt(SUBCHUNK2ID_DATA);
        buf.order(LITTLE_ENDIAN);
        buf.putInt(subChunk2Size);
    }

    public static RiffHeader fromStream(InputStream wavStream) throws IOException {
        byte[] header = new byte[RIFF_HEADER_LENGTH];
        int read = wavStream.read(header);
        if (read != RIFF_HEADER_LENGTH) {
            throw new IllegalArgumentException("Unable to read " + RIFF_HEADER_LENGTH + " bytes of RIFF header from stream");
        }
        return new RiffHeader(header);
    }
}
