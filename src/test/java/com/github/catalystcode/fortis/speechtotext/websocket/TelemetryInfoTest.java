package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.telemetry.AudioTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.CallsTelemetry;
import com.github.catalystcode.fortis.speechtotext.telemetry.ConnectionTelemetry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMetrics.*;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelemetryInfoTest {
    @Test
    void canBeConvertedToJson() {
        String telemetryJson = setupTelemetry("canBeConvertedToJson");
        JSONObject telemetry = new JSONObject(telemetryJson);

        verifyReceivedMessages(telemetry);
        verifyMetrics(telemetry);
    }

    private String setupTelemetry(String testName) {
        String connectionId = getClass().getName() + "-" + testName + "-connectionId";
        String requestId = getClass().getName() + "-canBeConvertedToJson-requestId";
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
        Set<String> metricNames = new HashSet<>();
        for (Object obj : metrics) {
            JSONObject metric = (JSONObject) obj;
            metricNames.add(metric.getString(NAME));
        }
        assertEquals(2, metricNames.size());
        assertTrue(metricNames.contains(CONNECTION_METRIC));
        assertTrue(metricNames.contains(MICROPHONE_METRIC));
    }
}