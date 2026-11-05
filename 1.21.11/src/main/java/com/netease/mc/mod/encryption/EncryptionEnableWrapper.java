package com.netease.mc.mod.encryption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptionEnableWrapper {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Boolean NetworkEncrypt = true;
    public static int NetworkCompressionThreshold = -100;
}