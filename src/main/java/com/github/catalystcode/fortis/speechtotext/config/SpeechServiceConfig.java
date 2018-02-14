package com.github.catalystcode.fortis.speechtotext.config;

import java.util.Locale;

import static com.github.catalystcode.fortis.speechtotext.utils.Environment.getSpeechPlatformHost;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceConnectionHeaders.*;

public class SpeechServiceConfig {
    private final String subscriptionKey;
    private final SpeechType speechType;
    private final OutputFormat outputFormat;
    private final Locale locale;
    private final String host;

    public SpeechServiceConfig(String subscriptionKey, SpeechType speechType, OutputFormat outputFormat, Locale locale) {
        this.subscriptionKey = subscriptionKey;
        this.speechType = speechType;
        this.outputFormat = outputFormat;
        this.locale = locale;
        this.host = getSpeechPlatformHost();
    }

    public String getConnectionUrl(String connectionId) {
        return host + speechType.endpoint +
            '?' + LANGUAGE + '=' + locale.toLanguageTag() +
            '&' + FORMAT + '=' + outputFormat.value +
            '&' + CONNECTION_ID + '=' + connectionId +
            '&' + SUBSCRIPTION_KEY + '=' + subscriptionKey;
    }
}
