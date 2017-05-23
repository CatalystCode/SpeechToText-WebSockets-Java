package com.github.catalystcode.fortis.speechtotext.websocket;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.Locale;
import java.util.concurrent.Future;

public class SpeechServiceClient {
    private static final Logger log = Logger.getLogger(SpeechServiceClient.class);
    private static final String host = "wss://speech.platform.bing.com";
    private final String key;
    private final Endpoint endpoint;
    private final Format format;
    private final Locale locale;
    private final MessageHandler handler;
    private final String connectionId;
    private WebSocketClient client;

    public SpeechServiceClient(String key, Endpoint endpoint, Format format, Locale locale, MessageHandler handler) {
        this.key = key;
        this.endpoint = endpoint;
        this.format = format;
        this.locale = locale;
        this.handler = handler;
        this.connectionId = ProtocolUtils.newGuid();
    }

    public Future<Session> start() throws Exception {
        client = new WebSocketClient(new SslContextFactory());
        client.start();
        String serverUrl = getServerUrl();
        log.debug("Connecting to " + serverUrl);
        return client.connect(handler, URI.create(serverUrl), new ClientUpgradeRequest());
    }

    public void stop() throws Exception {
        client.stop();
    }

    private String getServerUrl() {
        return host + endpoint.path +
            "?language=" + locale.toString() +
            "&format=" + format.value +
            "&X-ConnectionId=" + connectionId +
            "&Ocp-Apim-Subscription-Key=" + key;
    }
}
