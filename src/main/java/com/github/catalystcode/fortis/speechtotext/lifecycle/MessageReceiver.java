package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.telemetry.CallsTelemetry;
import com.github.catalystcode.fortis.speechtotext.utils.Func;
import com.github.catalystcode.fortis.speechtotext.messages.MessageParser;
import com.github.catalystcode.fortis.speechtotext.websocket.MessageSender;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.PATH;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.REQUEST_ID;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.*;


public class MessageReceiver {
    private static final Logger log = Logger.getLogger(MessageReceiver.class);
    private final Func<String> onResult;
    private final Func<String> onHypothesis;
    private final CountDownLatch endLatch;
    private MessageSender sender;

    public MessageReceiver(Func<String> onResult, Func<String> onHypothesis, CountDownLatch endLatch) {
        this.onResult = onResult;
        this.onHypothesis = onHypothesis;
        this.endLatch = endLatch;
    }

    public void onMessage(String message) {
        Map<String, String> headers = MessageParser.parseHeaders(message);
        JSONObject body = MessageParser.parseBody(message);

        String path = headers.get(PATH);
        String requestId = headers.get(REQUEST_ID);
        CallsTelemetry.forId(requestId).recordCall(path);
        log.debug("Got message at path " + path + " with payload '" + body + "'");

        if (SPEECH_HYPOTHESIS.equalsIgnoreCase(path)) {
            SpeechHypothesisMessage.handle(body, onHypothesis);
        } else if (SPEECH_PHRASE.equalsIgnoreCase(path)) {
            SpeechPhraseMessage.handle(body, onResult);
        } else if (TURN_END.equalsIgnoreCase(path)) {
            TurnEndMessage.handle(sender, endLatch);
        }
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
