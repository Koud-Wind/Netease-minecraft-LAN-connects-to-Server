package com.netease.mc.mod.playermanager;

import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.playermanager.reply.ReplySearchPlayerListByName;
import net.neoforged.neoforge.common.NeoForge;

public class PlayerManagerMod {
    public static void init() {
        NetworkHandler.networkHandler.register(ReplySearchPlayerListByName.SMID, new ReplySearchPlayerListByName());
        NeoForge.EVENT_BUS.register(new EventHandlerClient());
    }
}
