package com.github.catalystcode.fortis.speechtotext.websocket;

import org.json.JSONObject;

import static java.lang.System.getProperty;

@SuppressWarnings("unused")
public class PlatformInfo {
    public Context getContext() { return new Context(); }

    public class Context {
        public final System getSystem() { return new System(); }
        public final Os getOs() { return new Os(); }
        public final Device getDevice() { return new Device(); }

        public class System {
            public final String getVersion() { return getenv("STTWS_SYSTEM_VERSION", "0.0.1"); }
        }

        public class Os {
            public final String getPlatform() { return getProperty("os.name").split(" ")[0]; }
            public final String getName() { return getProperty("os.name"); }
            public final String getVersion() { return getProperty("os.version"); }
        }

        public class Device {
            public final String getManufacturer() { return getenv("SSTWS_DEVICE_MANUFACTURER", "SpeechToText-Websockets-Java"); }
            public final String getModel() { return getenv("SSTWS_DEVICE_MODEL", "SpeechToText-Websockets-Java"); }
            public final String getVersion() { return getenv("SSTWS_DEVICE_VERSION", "0.0.1"); }
        }
    }

    String toJson() {
        return new JSONObject(this).toString();
    }

    private static String getenv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
}
