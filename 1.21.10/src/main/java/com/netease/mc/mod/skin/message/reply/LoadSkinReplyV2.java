package com.netease.mc.mod.skin.message.reply;

import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.skin.SkinHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadSkinReplyV2 extends Reply {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int SMID = 2050;

    public void handler(String name, String skinPath, String capePath, int isSlim) {
        if (!SkinHandler.lockObjectMap.containsKey(name)) {
            LOGGER.error("[Skin]: name:" + name + " do not exist!");
            return;
        }
        synchronized (SkinHandler.lockObjectMap.get(name)) {
            try {
                SkinHandler.nameSkinMap.put(name, skinPath);
                SkinHandler.nameCapeMap.put(name, capePath);
                SkinHandler.nameSkinMode.put(name, Boolean.valueOf(isSlim == 1));
                SkinHandler.lockObjectMap.get(name).notify();
            } catch (Exception e) {
                LOGGER.error("LoadSkinReply2", e);
            }
        }
    }
}
