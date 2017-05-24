package com.github.catalystcode.fortis.speechtotext.websocket;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;

import static com.github.catalystcode.fortis.speechtotext.websocket.Headers.Path;
import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.parseBody;
import static com.github.catalystcode.fortis.speechtotext.websocket.MessageUtils.parseHeaders;

public class MessageReceiver {
    private static final Logger log = Logger.getLogger(MessageReceiver.class);

    public void onMessage(String message) {
        Map<String, String> headers = parseHeaders(message);
        JSONObject body = parseBody(message);
        log.info("Got message at path " + headers.get(Path) + " with payload '" + body + "'");
    }
}
