package com.netease.mc.mod.playermanager;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("preInit at CommonProxy");
    }

    public void init(FMLInitializationEvent event) {
        System.out.println("init at CommonProxy");
    }

    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("postInit at CommonProxy");
    }
}