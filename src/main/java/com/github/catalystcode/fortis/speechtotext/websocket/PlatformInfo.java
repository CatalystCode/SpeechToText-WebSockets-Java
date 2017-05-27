package com.github.catalystcode.fortis.speechtotext.websocket;

import org.json.JSONObject;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceSpeechConfig.*;
import static java.lang.System.getProperty;

class PlatformInfo {
    String toJson() {
        JSONObject json = new JSONObject();
        json.put(CONTEXT, createContext());
        return json.toString();
    }

    private JSONObject createContext() {
        JSONObject json = new JSONObject();
        json.put(SYSTEM, createSystem());
        json.put(OS, createOs());
        json.put(DEVICE, createDevice());
        return json;
    }

    private JSONObject createSystem() {
        JSONObject json = new JSONObject();
        json.put(SYSTEM_VERSION, getenv("STTWS_SYSTEM_VERSION", "0.0.1"));
        return json;
    }

    private JSONObject createOs() {
        JSONObject json = new JSONObject();
        json.put(OS_PLATFORM, getProperty("os.name").split(" ")[0]);
        json.put(OS_NAME, getProperty("os.name"));
        json.put(OS_VERSION, getProperty("os.version"));
        return json;
    }

    private JSONObject createDevice() {
        JSONObject json = new JSONObject();
        json.put(DEVICE_MANUFACTURER, getenv("SSTWS_DEVICE_MANUFACTURER", "SpeechToText-Websockets-Java"));
        json.put(DEVICE_MODEL, getenv("SSTWS_DEVICE_MODEL", "SpeechToText-Websockets-Java"));
        json.put(DEVICE_VERSION, getenv("SSTWS_DEVICE_VERSION", "0.0.1"));
        return json;
    }

    private static String getenv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
}
