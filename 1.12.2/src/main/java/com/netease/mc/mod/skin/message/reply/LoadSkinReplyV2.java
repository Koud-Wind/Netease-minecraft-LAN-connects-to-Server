package com.netease.mc.mod.skin.message.reply;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.skin.SkinHandler;

public class LoadSkinReplyV2 extends Reply {
    public static final int SMID = 2050;

    public void handler(String name, String skinPath, String capePath, int isSlim) {
        if (!SkinHandler.lockObjectMap.containsKey(name)) {
            Common.Log("[Skin]: name:" + name + " do not exist!");
            return;
        }
        synchronized (SkinHandler.lockObjectMap.get(name)) {
            try {
                SkinHandler.nameSkinMap.put(name, skinPath);
                SkinHandler.nameCapeMap.put(name, capePath);
                SkinHandler.nameSkinMode.put(name, Boolean.valueOf(isSlim == 1));
                SkinHandler.lockObjectMap.get(name).notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}