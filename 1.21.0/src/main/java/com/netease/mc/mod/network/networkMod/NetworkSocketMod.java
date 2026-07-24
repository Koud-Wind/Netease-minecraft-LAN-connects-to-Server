package com.netease.mc.mod.network.networkMod;

import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.socket.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;

public class NetworkSocketMod {
    public static void init() {
        GameState.gameState = GameState.GameS.INIT;
        NetworkHandler.networkHandler.register(1, new ReplyCloseMinecraft());
        MinecraftForge.EVENT_BUS.register(NetworkHandler.networkHandler);
        MinecraftForge.EVENT_BUS.register(new ClientNetworkHandler());
    }
}