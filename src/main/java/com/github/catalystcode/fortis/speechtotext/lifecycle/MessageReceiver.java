package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.utils.Func;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.PATH;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.SPEECH_PHRASE;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.TURN_END;
import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.parseBody;
import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.parseHeaders;


public class MessageReceiver {
    private static final Logger log = Logger.getLogger(MessageReceiver.class);
    private final Func<String> onResult;
    private final CountDownLatch endLatch;

    public MessageReceiver(Func<String> onResult, CountDownLatch endLatch) {
        this.onResult = onResult;
        this.endLatch = endLatch;
    }

    public void onMessage(String message) {
        Map<String, String> headers = parseHeaders(message);
        JSONObject body = parseBody(message);

        String path = headers.get(PATH);
        log.info("Got message at path " + path + " with payload '" + body + "'");

        if (SPEECH_PHRASE.equalsIgnoreCase(path)) {
            SpeechPhraseMessage.handle(body, onResult);
        } else if (TURN_END.equalsIgnoreCase(path)) {
            TurnEndMessage.handle(body, endLatch);
        } else {
            log.warn("Unhandled message at path " + path);
        }
    }
}
