package com.github.catalystcode.fortis.speechtotext.websocket;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.catalystcode.fortis.speechtotext.websocket.Headers.ContentType;
import static com.github.catalystcode.fortis.speechtotext.websocket.Headers.Path;
import static com.github.catalystcode.fortis.speechtotext.websocket.Headers.RequestId;
import static org.junit.jupiter.api.Assertions.*;

class MessageUtilsTest {
    private static final String turnStartMessage = "" +
        "X-RequestId:e7a1b5d70b814aab8e5f43d9bc3fbf96\r\n" +
        "Content-Type:application/json; charset=utf-8\r\n" +
        "Path: turn.start\r\n" +
        "\r\n" +
        "{\r\n" +
        "  \"context\": {\r\n" +
        "    \"serviceTag\": \"04319a8c660a4d1e8b0ba640d9b9c6ed\"\r\n" +
        "  }\r\n" +
        "}";

    @Test
    void parseHeaders() {
        Map<String, String> headers = MessageUtils.parseHeaders(turnStartMessage);
        assertEquals(3, headers.size());
        assertEquals("turn.start", headers.get(Path));
        assertEquals("application/json; charset=utf-8", headers.get(ContentType));
        assertEquals("e7a1b5d70b814aab8e5f43d9bc3fbf96", headers.get(RequestId));
    }

    @Test
    void parseBody() {
        JSONObject body = MessageUtils.parseBody(turnStartMessage);
        assertEquals("04319a8c660a4d1e8b0ba640d9b9c6ed", body.getJSONObject("context").getString("serviceTag"));
    }
}