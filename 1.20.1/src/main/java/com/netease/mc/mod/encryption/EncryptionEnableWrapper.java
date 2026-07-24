package com.netease.mc.mod.encryption;

import javax.crypto.Cipher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptionEnableWrapper {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Boolean NetworkEncrypt = true;
    public static int NetworkCompressionThreshold = -100;

    public static void setEncryptionKeyWrapper(IConnectionOld old, Cipher decryptCipher, Cipher encryptCipher) {
        LOGGER.info("setEncryptionKeyWrapper: " + NetworkEncrypt);
        if (NetworkEncrypt.booleanValue()) {
            old.setEncryptionKeyOld(decryptCipher, encryptCipher);
        }
    }

    public static int getCompressionThresholdWrapper(IMinecraftServerOld old) {
        int newThreshold = NetworkCompressionThreshold >= -1 ? NetworkCompressionThreshold : old.getCompressionThresholdOld();
        LOGGER.info("getCompressionThresholdWrapper: " + newThreshold);
        return newThreshold;
    }
}