package com.github.catalystcode.fortis.speechtotext.config;

import java.util.Locale;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceConnectionHeaders.*;

public class SpeechServiceConfig {
    private final String subscriptionKey;
    private final Endpoint endpoint;
    private final Format format;
    private final Locale locale;
    private final String host;

    public SpeechServiceConfig(String subscriptionKey, Endpoint endpoint, Format format, Locale locale) {
        this.subscriptionKey = subscriptionKey;
        this.endpoint = endpoint;
        this.format = format;
        this.locale = locale;
        this.host = "wss://speech.platform.bing.com";
    }

    public String getConnectionUrl(String connectionId) {
        return host + endpoint.path +
            '?' + LANGUAGE + '=' + locale.toString() +
            '&' + FORMAT + '=' + format.value +
            '&' + CONNECTION_ID + '=' + connectionId +
            '&' + SUBSCRIPTION_KEY + '=' + subscriptionKey;
    }
}
