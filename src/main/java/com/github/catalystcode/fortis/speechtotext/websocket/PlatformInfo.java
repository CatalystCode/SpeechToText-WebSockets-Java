package com.github.catalystcode.fortis.speechtotext.websocket;

import org.json.JSONObject;

import static java.lang.System.getProperty;

class PlatformInfo {
    String toJson() {
        JSONObject json = new JSONObject();
        json.put("context", createContext());
        return json.toString();
    }

    private JSONObject createContext() {
        JSONObject json = new JSONObject();
        json.put("system", createSystem());
        json.put("os", createOs());
        json.put("device", createDevice());
        return json;
    }

    private JSONObject createSystem() {
        JSONObject json = new JSONObject();
        json.put("version", getenv("STTWS_SYSTEM_VERSION", "0.0.1"));
        return json;
    }

    private JSONObject createOs() {
        JSONObject json = new JSONObject();
        json.put("platform", getProperty("os.name").split(" ")[0]);
        json.put("name", getProperty("os.name"));
        json.put("version", getProperty("os.version"));
        return json;
    }

    private JSONObject createDevice() {
        JSONObject json = new JSONObject();
        json.put("manufacturer", getenv("SSTWS_DEVICE_MANUFACTURER", "SpeechToText-Websockets-Java"));
        json.put("model", getenv("SSTWS_DEVICE_MODEL", "SpeechToText-Websockets-Java"));
        json.put("version", getenv("SSTWS_DEVICE_VERSION", "0.0.1"));
        return json;
    }

    private static String getenv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
}
