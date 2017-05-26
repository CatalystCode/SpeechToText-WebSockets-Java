package com.github.catalystcode.fortis.speechtotext.config;

import java.util.Locale;

import static com.github.catalystcode.fortis.speechtotext.utils.ProtocolUtils.newGuid;

public class SpeechServiceConfig {
    private static final String host = "wss://speech.platform.bing.com";
    private final String key;
    private final Endpoint endpoint;
    private final Format format;
    private final Locale locale;
    private final String connectionId;

    public SpeechServiceConfig(String key, Endpoint endpoint, Format format, Locale locale) {
        this.key = key;
        this.endpoint = endpoint;
        this.format = format;
        this.locale = locale;
        this.connectionId = newGuid();
    }

    public String getConnectionUrl() {
        return host + endpoint.path +
            "?language=" + locale.toString() +
            "&format=" + format.value +
            "&X-ConnectionId=" + connectionId +
            "&Ocp-Apim-Subscription-Key=" + key;
    }
}
