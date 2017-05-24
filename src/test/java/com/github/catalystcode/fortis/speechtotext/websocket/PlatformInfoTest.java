package com.github.catalystcode.fortis.speechtotext.websocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformInfoTest {
    @Test
    void canBeConvertedToJson() {
        String config = new PlatformInfo().toJson();
        assertNotNull(config);
        assertNotEquals("", config);
        assertNotEquals("{}", config);
    }
}