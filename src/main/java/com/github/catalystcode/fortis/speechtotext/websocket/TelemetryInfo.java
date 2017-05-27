package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.telemetry.CallsTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceLimitations.MAX_ERROR_MESSAGE_NUM_CHARACTERS;

class TelemetryInfo {
    private final String connectionId;
    private final String requestId;

    TelemetryInfo(String connectionId, String requestId) {
        this.connectionId = connectionId;
        this.requestId = requestId;
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
        metrics.add(createListeningTriggerMetric());
        metrics.add(createMicrophoneMetric());
        json.put("Metrics", metrics);
    }

    private void putReceivedMessages(JSONObject json) {
        Map<String, Queue<String>> callTimestamps = CallsTelemetry.forId(requestId).getCallTimestamps();
        Collection<JSONObject> receivedMessages = new ArrayList<>(callTimestamps.size());
        for (Map.Entry<String, Queue<String>> entry : callTimestamps.entrySet()) {
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

        json.put("ReceivedMessages", receivedMessages);
    }

    private JSONObject createListeningTriggerMetric() {
        JSONObject metric = new JSONObject();
        metric.put("Name", "Microphone");
        metric.put("Start", "todo"); // todo
        metric.put("End", "todo"); // todo
        metric.put("Error", "todo"); // todo
        return metric;
    }

    private JSONObject createConnectionMetric() {
        ConnectionTelemetry telemetry = ConnectionTelemetry.forId(connectionId);

        JSONObject metric = new JSONObject();
        metric.put("Name", "Connection");
        metric.put("Id", connectionId);
        metric.put("Start", telemetry.getConnectionStarted());
        metric.put("End", telemetry.getConnectionEstablished());
        addError(metric, telemetry.getConnectionErrored());
        return metric;
    }

    private JSONObject createMicrophoneMetric() {
        JSONObject metric = new JSONObject();
        metric.put("Name", "Microphone");
        metric.put("Start", "todo"); // todo
        metric.put("End", "todo"); // todo
        metric.put("Error", "todo"); // todo
        return metric;
    }

    private void addError(JSONObject metric, String error) {
        if (error != null) {
            metric.put("Error", error.substring(0, MAX_ERROR_MESSAGE_NUM_CHARACTERS));
        }
    }
}
