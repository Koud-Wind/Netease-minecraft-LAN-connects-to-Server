package com.netease.mc.mod.network.socket;

import com.netease.mc.mod.network.common.GameState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

@Mod(modid = NetworkSocketMod.MODID, version = NetworkSocketMod.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class NetworkSocketMod {
    public static final String MODID = "networkmod";
    public static final String VERSION = "1.11.2";
    public static NetworkHandler networkHandler = new NetworkHandler();

    @SidedProxy(serverSide = "com.netease.mc.mod.network.socket.NetworkCommonProxy", clientSide = "com.netease.mc.mod.network.socket.NetworkClientProxy")
    public static NetworkCommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameState.gameState = GameState.GameS.INIT;
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        proxy.onServerStarted(event);
    }
}