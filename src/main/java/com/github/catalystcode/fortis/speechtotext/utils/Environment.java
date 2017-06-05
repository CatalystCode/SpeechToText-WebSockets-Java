package com.github.catalystcode.fortis.speechtotext.utils;

import static com.github.catalystcode.fortis.speechtotext.constants.EnvironmentVariables.*;
import static com.github.catalystcode.fortis.speechtotext.utils.Units.KB;
import static java.lang.Integer.parseInt;

public final class Environment {
    private Environment() {}

    public static String getSpeechPlatformHost() {
        return getenv(HOST, "wss://speech.platform.bing.com");
    }

    public static String getLibraryVersion() {
        return getenv(LIBRARY_VERSION, "0.0.1");
    }

    public static String getDeviceManufacturer() {
        return getenv(DEVICE_MANUFACTURER, "SpeechToText-Websockets-Java");
    }

    public static String getDeviceModel() {
        return getenv(DEVICE_MODEL, "SpeechToText-Websockets-Java");
    }

    public static String getDeviceVersion() {
        return getenv(DEVICE_VERSION, "0.0.1");
    }

    public static int getMp3BufferSize() {
        return parseInt(getenv(MP3_BUFFER_SIZE, String.valueOf(256 * KB)));
    }

    private static String getenv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
}
