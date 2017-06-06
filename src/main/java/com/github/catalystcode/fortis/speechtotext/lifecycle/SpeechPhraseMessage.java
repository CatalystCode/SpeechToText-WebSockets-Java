package com.github.catalystcode.fortis.speechtotext.lifecycle;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.function.Consumer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.*;

final class SpeechPhraseMessage {
    private static final Logger log = Logger.getLogger(SpeechPhraseMessage.class);
    private SpeechPhraseMessage() {}

    static void handle(JSONObject message, Consumer<String> onResult) {
        if (!isSuccess(message)) {
            return;
        }

        String displayText = message.getString(DISPLAY_TEXT);
        onResult.accept(displayText);
    }

    private static boolean isSuccess(JSONObject message) {
        String status = message.getString(RECOGNITION_STATUS);

        if (END_OF_DICTATION_STATUS.equalsIgnoreCase(status) ||
            END_OF_DICTATION_SILENCE_STATUS.equalsIgnoreCase(status)) {
            log.info("Detected end of speech");
            return false;
        }

        if (!SUCCESS_STATUS.equalsIgnoreCase(status)) {
            log.warn("Unable to recognize audio: " + message);
            return false;
        }

        return true;
    }
}
