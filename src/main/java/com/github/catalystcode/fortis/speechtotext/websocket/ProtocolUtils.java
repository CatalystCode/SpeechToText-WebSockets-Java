package com.github.catalystcode.fortis.speechtotext.websocket;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

final class ProtocolUtils {
    private ProtocolUtils() {}

    static String newGuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    static String newTimestamp() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }
}
