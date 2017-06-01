package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.utils.Func;
import org.json.JSONObject;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.HYPOTHESIS_TEXT;

final class SpeechHypothesisMessage {
    private SpeechHypothesisMessage() {}

    static void handle(JSONObject message, Func<String> onHypothesis) {
        if (onHypothesis == null) {
            return;
        }

        String displayText = message.getString(HYPOTHESIS_TEXT);
        onHypothesis.call(displayText);
    }
}
