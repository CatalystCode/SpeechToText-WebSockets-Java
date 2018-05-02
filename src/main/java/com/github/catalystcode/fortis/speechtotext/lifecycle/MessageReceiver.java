package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.messages.MessageParser;
import com.github.catalystcode.fortis.speechtotext.telemetry.CallsTelemetry;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.PATH;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.REQUEST_ID;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.*;


public class MessageReceiver {
    private static final Logger log = Logger.getLogger(MessageReceiver.class);
    private final Consumer<String> onResult;
    private final Consumer<String> onHypothesis;
    private final Consumer<String> onTurnStart;
    private final Runnable onTurnEnd;
    private final CountDownLatch endLatch;
    private MessageSender sender;

    public MessageReceiver(Consumer<String> onResult, Consumer<String> onHypothesis, CountDownLatch endLatch) {
        this(onResult, onHypothesis, null, null, endLatch);
    }

    public MessageReceiver(Consumer<String> onResult, Consumer<String> onHypothesis,
            Consumer<String> onTurnStart, Runnable onTurnEnd, CountDownLatch endLatch) {
        this.onResult = onResult;
        this.onHypothesis = onHypothesis;
        this.onTurnStart = onTurnStart;
        this.onTurnEnd = onTurnEnd;
        this.endLatch = endLatch;
    }

    public void onMessage(String message) {
        Map<String, String> headers = MessageParser.parseHeaders(message);
        JSONObject body = MessageParser.parseBody(message);

        String path = headers.get(PATH);
        String requestId = headers.get(REQUEST_ID);
        CallsTelemetry.forId(requestId).recordCall(path);
        log.debug("Got message at path " + path + " with payload '" + body + "'");

        if (TURN_START.equalsIgnoreCase(path)) {
            TurnStartMessage.handle(body, onTurnStart);
        } else if (SPEECH_HYPOTHESIS.equalsIgnoreCase(path)) {
            SpeechHypothesisMessage.handle(body, onHypothesis);
        } else if (SPEECH_PHRASE.equalsIgnoreCase(path)) {
            SpeechPhraseMessage.handle(body, onResult);
        } else if (TURN_END.equalsIgnoreCase(path)) {
            TurnEndMessage.handle(sender, endLatch, onTurnEnd);
        }
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
