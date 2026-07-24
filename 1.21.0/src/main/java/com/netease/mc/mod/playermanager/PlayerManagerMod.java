package com.netease.mc.mod.playermanager;

import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.playermanager.reply.ReplySearchPlayerListByName;
import net.minecraftforge.common.MinecraftForge;

public class PlayerManagerMod {
    public static void init() {
        NetworkHandler.networkHandler.register(ReplySearchPlayerListByName.SMID, new ReplySearchPlayerListByName());
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
    }
}