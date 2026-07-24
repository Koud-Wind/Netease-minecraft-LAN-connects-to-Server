package com.netease.mc.mod.network.socket;

import com.netease.mc.mod.network.common.GameParams;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class NetworkClientProxy extends NetworkCommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("preinit");
        GameParams.init();
        NetworkSocketMod.networkHandler = NetworkHandler.networkHandler;
        NetworkHandler.networkHandler.register(1, new ReplyCloseMinecraft());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        System.out.println("init");
        MinecraftForge.EVENT_BUS.register(new NetworkHandler());
        MinecraftForge.EVENT_BUS.register(new ClientNetworkHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("postinit");
    }
}