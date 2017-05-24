package com.github.catalystcode.fortis.speechtotext.websocket;

import java.net.URI;
import java.util.Locale;

import static com.github.catalystcode.fortis.speechtotext.websocket.ProtocolUtils.newGuid;

public class SpeechServiceUrl {
    private static final String host = "wss://speech.platform.bing.com";
    private final String key;
    private final Endpoint endpoint;
    private final Format format;
    private final Locale locale;
    private final String connectionId;

    public SpeechServiceUrl(String key, Endpoint endpoint, Format format, Locale locale) {
        this.key = key;
        this.endpoint = endpoint;
        this.format = format;
        this.locale = locale;
        this.connectionId = newGuid();
    }

    URI toURI() {
        return URI.create(formatWebSocketUrl());
    }

    private String formatWebSocketUrl() {
        return host + endpoint.path +
            "?language=" + locale.toString() +
            "&format=" + format.value +
            "&X-ConnectionId=" + connectionId +
            "&Ocp-Apim-Subscription-Key=" + key;
    }
}
