package com.github.catalystcode.fortis.speechtotext.constants;

public final class SpeechServiceLimitations {
    private SpeechServiceLimitations() {}

    public final static int MAX_ERROR_MESSAGE_NUM_CHARACTERS = 50;
    public static final int MAX_BYTES_PER_AUDIO_CHUNK = 8192;
    public static final int SAMPLE_RATE = 16000;
    public static final short NUM_CHANNELS = 1;
}
