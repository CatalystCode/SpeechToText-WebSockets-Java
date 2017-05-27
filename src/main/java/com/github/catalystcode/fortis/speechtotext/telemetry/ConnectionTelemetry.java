package com.github.catalystcode.fortis.speechtotext.telemetry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newTimestamp;

public final class ConnectionTelemetry {
    private static final ConcurrentMap<String, ConnectionTelemetry> POOL = new ConcurrentHashMap<>();

    private String connectionStarted;
    private String connectionEstablished;
    private String connectionErrored;

    private ConnectionTelemetry() {}

    public void recordConnectionStarted() {
        if (connectionStarted != null) {
            connectionStarted = newTimestamp();
        }
    }

    public void recordConnectionEstablished() {
        if (connectionEstablished != null) {
            connectionEstablished = newTimestamp();
        }
    }

    public void recordConnectionFailed(String message) {
        connectionEstablished = newTimestamp();
        connectionErrored = message;
    }

    public String getConnectionErrored() {
        return connectionErrored;
    }

    public String getConnectionEstablished() {
        return connectionEstablished;
    }

    public String getConnectionStarted() {
        return connectionStarted;
    }

    public static ConnectionTelemetry forId(String connectionId) {
        ConnectionTelemetry instance = POOL.get(connectionId);
        if (instance == null) {
            ConnectionTelemetry newInstance = new ConnectionTelemetry();
            instance = POOL.putIfAbsent(connectionId, newInstance);
            if (instance == null) {
                instance = newInstance;
            }
        }
        return instance;
    }
}
