package com.github.catalystcode.fortis.speechtotext.telemetry;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newTimestamp;

public final class CallsTelemetry {
    private static final ConcurrentMap<String, CallsTelemetry> POOL = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Queue<String>> callTimestamps = new ConcurrentHashMap<>();

    private CallsTelemetry() {}

    public void recordCall(String endpoint) {
        String now = newTimestamp();
        Queue<String> timestamps = callTimestamps.get(endpoint);
        if (timestamps == null) {
            Queue<String> newTimestamps = new ConcurrentLinkedQueue<>();
            timestamps = callTimestamps.putIfAbsent(endpoint, newTimestamps);
            if (timestamps == null) {
                timestamps = newTimestamps;
            }
        }
        timestamps.add(now);
    }

    public Map<String, Queue<String>> getCallTimestamps() {
        return callTimestamps;
    }

    public static CallsTelemetry forId(String requestId) {
        CallsTelemetry instance = POOL.get(requestId);
        if (instance == null) {
            CallsTelemetry newInstance = new CallsTelemetry();
            instance = POOL.putIfAbsent(requestId, newInstance);
            if (instance == null) {
                instance = newInstance;
            }
        }
        return instance;
    }
}
