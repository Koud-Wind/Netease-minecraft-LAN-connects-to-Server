//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.netease.mc.mod.filter;

import com.netease.mc.mod.filter.old.IMinecraftServerOld;
import com.netease.mc.mod.filter.old.INetworkManagerOld;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.crypto.SecretKey;

import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilterWrapper {
    private static final Logger LOGGER = LogManager.getLogger();
    public static Boolean NetworkEncrypt = true;
    public static int NetworkCompressionThreshold = -100;

    public static void enableEncryptionWrapper(INetworkManagerOld old, SecretKey key) {
        try {
            LOGGER.info("enableEncryptionWrapper: " + NetworkEncrypt);
            if (NetworkEncrypt) {
                old.enableEncryptionOld(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int getNetworkCompressionThresholdWrapper(IMinecraftServerOld old) {
        int newThreshold = NetworkCompressionThreshold;
        LOGGER.info("getNetworkCompressionThresholdWrapper: " + newThreshold);
        return newThreshold;
    }
}
