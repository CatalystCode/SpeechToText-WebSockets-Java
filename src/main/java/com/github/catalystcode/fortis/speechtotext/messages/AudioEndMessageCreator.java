package com.github.catalystcode.fortis.speechtotext.messages;

import java.nio.ByteBuffer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceContentTypes.WAV;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.AUDIO;

public final class AudioEndMessageCreator {
    private AudioEndMessageCreator() {}

    private static final BinaryMessageCreator binaryMessageCreator = new BinaryMessageCreator(false);

    public static ByteBuffer createAudioEndMessage(String requestId) {
        return binaryMessageCreator.createBinaryMessage(AUDIO, requestId, WAV, new byte[0], 0);
    }
}
