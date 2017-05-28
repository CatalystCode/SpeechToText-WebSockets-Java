package com.github.catalystcode.fortis.speechtotext.telemetry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newTimestamp;

public final class AudioTelemetry {
    private static final ConcurrentMap<String, AudioTelemetry> POOL = new ConcurrentHashMap<>();

    private String audioStarted;
    private String audioEnded;
    private String audioErrored;

    private AudioTelemetry() {}

    public void recordAudioStarted() {
        if (audioStarted == null) {
            audioStarted = newTimestamp();
        }
    }

    public void recordAudioEnded() {
        if (audioEnded == null) {
            audioEnded = newTimestamp();
        }
    }

    public void recordAudioFailed(String message) {
        audioEnded = newTimestamp();
        audioErrored = message;
    }

    public String getAudioErrored() {
        return audioErrored;
    }

    public String getAudioEnded() {
        return audioEnded;
    }

    public String getAudioStarted() {
        return audioStarted;
    }

    public static AudioTelemetry forId(String requestId) {
        AudioTelemetry instance = POOL.get(requestId);
        if (instance == null) {
            AudioTelemetry newInstance = new AudioTelemetry();
            instance = POOL.putIfAbsent(requestId, newInstance);
            if (instance == null) {
                instance = newInstance;
            }
        }
        return instance;
    }
}
