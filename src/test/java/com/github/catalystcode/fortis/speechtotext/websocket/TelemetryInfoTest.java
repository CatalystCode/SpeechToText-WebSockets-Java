package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.telemetry.AudioTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.CallsTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMetrics.*;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TelemetryInfoTest {
    @Test
    void canBeConvertedToJson() {
        String telemetryJson = setupTelemetry("canBeConvertedToJson");
        JSONObject telemetry = new JSONObject(telemetryJson);

        verifyReceivedMessages(telemetry);
        verifyMetrics(telemetry);
    }

    @Test
    void sameTelemetryIsUsedForRequest() {
        String testName = "sameTelemetryIsUsedForRequest";
        String connectionId = newConnectionId(testName);
        String requestId = newRequestId(testName);

        ConnectionTelemetry connectionTelemetry1 = ConnectionTelemetry.forId(connectionId);
        ConnectionTelemetry connectionTelemetry2 = ConnectionTelemetry.forId("otherConnectionId");
        ConnectionTelemetry connectionTelemetry3 = ConnectionTelemetry.forId(connectionId);
        assertNotEquals(connectionTelemetry1, connectionTelemetry2);
        assertEquals(connectionTelemetry1, connectionTelemetry3);

        CallsTelemetry callsTelemetry1 = CallsTelemetry.forId(requestId);
        CallsTelemetry callsTelemetry2 = CallsTelemetry.forId("otherRequestId");
        CallsTelemetry callsTelemetry3 = CallsTelemetry.forId(requestId);
        assertNotEquals(callsTelemetry1, callsTelemetry2);
        assertEquals(callsTelemetry1, callsTelemetry3);

        CallsTelemetry audioTelemetry1 = CallsTelemetry.forId(requestId);
        CallsTelemetry audioTelemetry2 = CallsTelemetry.forId("otherRequestId");
        CallsTelemetry audioTelemetry3 = CallsTelemetry.forId(requestId);
        assertNotEquals(audioTelemetry1, audioTelemetry2);
        assertEquals(audioTelemetry1, audioTelemetry3);
    }

    private String setupTelemetry(String testName) {
        String connectionId = newConnectionId(testName);
        String requestId = newRequestId(testName);
        CallsTelemetry callsTelemetry = CallsTelemetry.forId(requestId);
        ConnectionTelemetry connectionTelemetry = ConnectionTelemetry.forId(connectionId);
        AudioTelemetry audioTelemetry = AudioTelemetry.forId(requestId);

        connectionTelemetry.recordConnectionStarted();
        connectionTelemetry.recordConnectionEstablished();
        audioTelemetry.recordAudioStarted();
        callsTelemetry.recordCall(TURN_START);
        callsTelemetry.recordCall(SPEECH_HYPOTHESIS);
        callsTelemetry.recordCall(SPEECH_HYPOTHESIS);
        callsTelemetry.recordCall(SPEECH_PHRASE);
        callsTelemetry.recordCall(SPEECH_END);
        callsTelemetry.recordCall(TURN_END);
        audioTelemetry.recordAudioEnded();

        return new TelemetryInfo(connectionId, callsTelemetry, connectionTelemetry, audioTelemetry).toJson();
    }

    private String newRequestId(String testName) {
        return getClass().getName() + "-" + testName + "-requestId";
    }

    private String newConnectionId(String testName) {
        return getClass().getName() + "-" + testName + "-connectionId";
    }

    private void verifyReceivedMessages(JSONObject telemetry) {
        JSONArray receivedMessages = telemetry.getJSONArray(RECEIVED_MESSAGES);
        for (Object obj : receivedMessages) {
            JSONObject receivedMessage = (JSONObject) obj;
            Set<String> keys = receivedMessage.keySet();
            assertEquals(1, keys.size());
            String key = keys.iterator().next();
            if (SPEECH_HYPOTHESIS.equalsIgnoreCase(key)) {
                JSONArray values = receivedMessage.getJSONArray(key);
                assertNotNull(values);
                assertEquals(2, values.length());
            } else {
                String value = receivedMessage.getString(key);
                assertNotNull(value);
            }
        }
    }

    private void verifyMetrics(JSONObject telemetry) {
        JSONArray metrics = telemetry.getJSONArray(METRICS);
        assertEquals(2, metrics.length());
        Map<String, JSONObject> parsedMetrics = new HashMap<>();
        for (Object obj : metrics) {
            JSONObject metric = (JSONObject) obj;
            parsedMetrics.put(metric.getString(NAME), metric);
        }
        assertEquals(2, parsedMetrics.size());
        JSONObject connectionMetric = parsedMetrics.get(CONNECTION_METRIC);
        JSONObject microphoneMetric = parsedMetrics.get(MICROPHONE_METRIC);
        assertNotNull(connectionMetric);
        assertNotNull(microphoneMetric);
        assertNotNull(connectionMetric.getString(START));
        assertNotNull(microphoneMetric.getString(START));
        assertNotNull(connectionMetric.getString(END));
        assertNotNull(microphoneMetric.getString(END));
    }
}