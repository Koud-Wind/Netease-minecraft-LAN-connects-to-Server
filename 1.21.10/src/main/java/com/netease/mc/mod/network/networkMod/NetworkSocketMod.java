package com.netease.mc.mod.network.networkMod;

import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.socket.NetworkHandler;
import net.neoforged.neoforge.common.NeoForge;

public class NetworkSocketMod {
    public static void init() {
        GameState.gameState = GameState.GameS.INIT;
        NetworkHandler.networkHandler.register(1, new ReplyCloseMinecraft());
        NeoForge.EVENT_BUS.register(new ClientNetworkHandler());
    }
}
