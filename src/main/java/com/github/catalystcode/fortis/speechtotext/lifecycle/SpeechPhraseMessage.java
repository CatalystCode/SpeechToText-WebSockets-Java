package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.utils.Func;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.*;

final class SpeechPhraseMessage {
    private static final Logger log = Logger.getLogger(SpeechPhraseMessage.class);
    private SpeechPhraseMessage() {}

    static void handle(JSONObject message, Func<String> onResult) {
        String status = message.getString(RECOGNITION_STATUS);

        if (!SUCCESS_STATUS.equalsIgnoreCase(status)) {
            log.warn("Unable to recognize audio: " + message);
            return;
        }

        String displayText = message.getString(DISPLAY_TEXT);
        onResult.call(displayText);
    }
}
