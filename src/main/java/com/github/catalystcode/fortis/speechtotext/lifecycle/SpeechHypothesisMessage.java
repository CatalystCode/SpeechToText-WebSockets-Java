package com.github.catalystcode.fortis.speechtotext.lifecycle;

import org.json.JSONObject;

import java.util.function.Consumer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.HYPOTHESIS_TEXT;

final class SpeechHypothesisMessage {
    private SpeechHypothesisMessage() {}

    static void handle(JSONObject message, Consumer<String> onHypothesis) {
        if (onHypothesis == null) {
            return;
        }

        String displayText = message.getString(HYPOTHESIS_TEXT);
        onHypothesis.accept(displayText);
    }
}
