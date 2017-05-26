package com.github.catalystcode.fortis.speechtotext.utils;

import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.util.UUID.randomUUID;

public final class ProtocolUtils {
    private ProtocolUtils() {}

    public static String newGuid() {
        return randomUUID().toString().replace("-", "");
    }

    public static String newTimestamp() {
        return now().format(ISO_INSTANT);
    }
}
