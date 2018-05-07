package com.github.catalystcode.fortis.speechtotext.lifecycle;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.*;

import java.util.function.Consumer;

import org.json.JSONObject;;

final class TurnStartMessage {
    private TurnStartMessage() {}

    static void handle(JSONObject message, Consumer<String> onTurnStart) {
        if (onTurnStart == null) {
            return;
        }

        JSONObject context = message.getJSONObject(CONTEXT);
        String serviceTag = context.getString(SERVICE_TAG);
        onTurnStart.accept(serviceTag);
    }
}
