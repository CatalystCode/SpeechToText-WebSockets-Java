package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.telemetry.AudioTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.CallsTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.MAX_ERROR_MESSAGE_NUM_CHARACTERS;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMetrics.*;

class TelemetryInfo {
    private final String connectionId;
    private final CallsTelemetry callsTelemetry;
    private final ConnectionTelemetry connectionTelemetry;
    private AudioTelemetry audioTelemetry;

    TelemetryInfo(String connectionId, CallsTelemetry callsTelemetry, ConnectionTelemetry connectionTelemetry, AudioTelemetry audioTelemetry) {
        this.connectionId = connectionId;
        this.callsTelemetry = callsTelemetry;
        this.connectionTelemetry = connectionTelemetry;
        this.audioTelemetry = audioTelemetry;
    }

    String toJson() {
        JSONObject json = new JSONObject();
        putReceivedMessages(json);
        putMetrics(json);
        return json.toString();
    }

    private void putMetrics(JSONObject json) {
        Collection<JSONObject> metrics = new ArrayList<>();
        metrics.add(createConnectionMetric());
        metrics.add(createMicrophoneMetric());
        json.put(METRICS, metrics);
    }

    private void putReceivedMessages(JSONObject json) {
        Collection<JSONObject> receivedMessages = new ArrayList<>();
        for (Map.Entry<String, Queue<String>> entry : callsTelemetry.getCallTimestamps().entrySet()) {
            String endpoint = entry.getKey();
            Queue<String> calls = entry.getValue();
            JSONObject receivedMessage = new JSONObject();
            if (calls.size() > 1) {
                receivedMessage.put(endpoint, calls);
            } else {
                receivedMessage.put(endpoint, calls.peek());
            }
            receivedMessages.add(receivedMessage);
        }
        json.put(RECEIVED_MESSAGES, receivedMessages);
    }

    private JSONObject createConnectionMetric() {
        JSONObject metric = new JSONObject();
        metric.put(NAME, CONNECTION_METRIC);
        metric.put(ID, connectionId);
        metric.put(START, connectionTelemetry.getConnectionStarted());
        metric.put(END, connectionTelemetry.getConnectionEstablished());
        addError(metric, connectionTelemetry.getConnectionErrored());
        return metric;
    }

    private JSONObject createMicrophoneMetric() {
        JSONObject metric = new JSONObject();
        metric.put(NAME, MICROPHONE_METRIC);
        metric.put(START, audioTelemetry.getAudioStarted());
        metric.put(END, audioTelemetry.getAudioEnded());
        addError(metric, audioTelemetry.getAudioErrored());
        return metric;
    }

    private void addError(JSONObject metric, String error) {
        if (error != null) {
            metric.put(ERROR, error.substring(0, MAX_ERROR_MESSAGE_NUM_CHARACTERS));
        }
    }
}
