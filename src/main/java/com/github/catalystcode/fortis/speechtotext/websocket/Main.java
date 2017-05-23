package com.github.catalystcode.fortis.speechtotext.websocket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ALL);
    }

    public static void main(String[] args) throws Exception {
    }
}
