package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.friendplay.message.reply.ReplyJoinRoom;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewClient;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewSingle;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewSingleV2;
import com.netease.mc.mod.friendplay.message.reply.ReplyOpScreenShot;
import com.netease.mc.mod.friendplay.message.reply.ReplyReconnect;
import com.netease.mc.mod.friendplay.message.reply.ReplyStartExistSingle;
import com.netease.mc.mod.network.socket.NetworkSocketMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class FriendPlayClientProxy extends FriendPlayCommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("preinit");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        System.out.println("init");
        MinecraftForge.EVENT_BUS.register(new GuiOpenEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerEnterWorldEventHandler());
        FMLCommonHandler.instance().bus().register(new PlayerEnterWorldEventHandler());
        NetworkSocketMod.networkHandler.register(ReplyJoinRoom.SMID, new ReplyJoinRoom());
        NetworkSocketMod.networkHandler.register(ReplyStartExistSingle.SMID, new ReplyStartExistSingle());
        NetworkSocketMod.networkHandler.register(ReplyNewClient.SMID, new ReplyNewClient());
        NetworkSocketMod.networkHandler.register(1537, new ReplyNewSingle());
        NetworkSocketMod.networkHandler.register(ReplyNewSingleV2.SMID, new ReplyNewSingleV2());
        NetworkSocketMod.networkHandler.register(ReplyReconnect.SMID, new ReplyReconnect());
        NetworkSocketMod.networkHandler.registerAsync(ReplyOpScreenShot.SMID, new ReplyOpScreenShot());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        System.out.println("postinit");
    }
}