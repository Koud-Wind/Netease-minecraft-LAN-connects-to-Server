package com.netease.mc.mod.playermanager;

import com.netease.mc.mod.network.socket.NetworkSocketMod;
import com.netease.mc.mod.playermanager.reply.ReplySearchPlayerListByName;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("preInit at ClientProxy");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
        NetworkSocketMod.networkHandler.register(ReplySearchPlayerListByName.SMID, new ReplySearchPlayerListByName());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }
}