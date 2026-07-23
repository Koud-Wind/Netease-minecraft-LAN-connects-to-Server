package com.netease.mc.mod.skin;

import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.skin.message.reply.LoadSkinReplyV2;

public class SkinMod {
    public static void init() {
        NetworkHandler.networkHandler.registerAsync(LoadSkinReplyV2.SMID, new LoadSkinReplyV2());
    }
}
