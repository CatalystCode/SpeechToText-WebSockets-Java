package com.github.catalystcode.fortis.speechtotext.websocket;

import com.github.catalystcode.fortis.speechtotext.utils.Func;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;

import static com.github.catalystcode.fortis.speechtotext.websocket.Headers.Path;
import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.parseBody;
import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.parseHeaders;

public class MessageReceiver {
    private static final Logger log = Logger.getLogger(MessageReceiver.class);
    private final Func<String> onResult;

    public MessageReceiver(Func<String> onResult) {
        this.onResult = onResult;
    }

    public void onMessage(String message) {
        Map<String, String> headers = parseHeaders(message);
        JSONObject body = parseBody(message);

        String path = headers.get(Path);
        log.info("Got message at path " + path + " with payload '" + body + "'");

        if ("speech.phrase".equalsIgnoreCase(path)) {
            onSpeechPhrase(body);
        } else {
            log.warn("Unhandled message at path " + path);
        }
    }

    private void onSpeechPhrase(JSONObject message) {
        String recognitionStatus = message.getString("RecognitionStatus");

        if (!"Success".equalsIgnoreCase(recognitionStatus)) {
            log.warn("Unable to recognize audio: " + message);
            return;
        }

        String displayText = message.getString("DisplayText");
        onResult.call(displayText);
    }
}
