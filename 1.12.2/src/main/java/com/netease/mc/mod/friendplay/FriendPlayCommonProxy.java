package com.netease.mc.mod.friendplay;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

public class FriendPlayCommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("preinit");
    }

    public void init(FMLInitializationEvent event) {
        System.out.println("init");
    }

    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("postinit");
    }

    public void onServerStarted(FMLServerStartedEvent event) {
        System.out.println("onServerStarted");
    }
}