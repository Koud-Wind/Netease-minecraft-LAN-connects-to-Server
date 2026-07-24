package com.netease.mc.mod.playermanager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = PlayerManagerMod.MODID, version = "1.0", acceptedMinecraftVersions = "[1.12.2]")
public class PlayerManagerMod {
    public static final String MODID = "playermanager";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "com.netease.mc.mod.playermanager.ClientProxy", serverSide = "com.netease.mc.mod.playermanager.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}