package com.github.catalystcode.fortis.speechtotext.websocket;

import org.json.JSONObject;

import static com.github.catalystcode.fortis.speechtotext.utils.Environment.*;
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
        json.put(SYSTEM_VERSION, getLibraryVersion());
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
        json.put(DEVICE_MANUFACTURER, getDeviceManufacturer());
        json.put(DEVICE_MODEL, getDeviceModel());
        json.put(DEVICE_VERSION, getDeviceVersion());
        return json;
    }
}
