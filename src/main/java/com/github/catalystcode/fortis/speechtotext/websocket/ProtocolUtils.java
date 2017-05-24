package com.github.catalystcode.fortis.speechtotext.websocket;

import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.util.UUID.randomUUID;

final class ProtocolUtils {
    private ProtocolUtils() {}

    static String newGuid() {
        return randomUUID().toString().replace("-", "");
    }

    static String newTimestamp() {
        return now().format(ISO_INSTANT);
    }
}
