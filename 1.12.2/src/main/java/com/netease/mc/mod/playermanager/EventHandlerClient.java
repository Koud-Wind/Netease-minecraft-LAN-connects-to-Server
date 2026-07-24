package com.netease.mc.mod.playermanager;

import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventHandlerClient {
    private static int ClientConnectedToServerCmd = 1042;

    @SubscribeEvent
    public void onClientConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        MessageRequest mrq = new MessageRequest();
        mrq.send(ClientConnectedToServerCmd, new Object[0]);
    }
}