package com.netease.mc.mod.playermanager;

import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerClient {
    private static int ClientConnectedToServerCmd = 1042;

    @SubscribeEvent
    public void OnPlayerLoginedEvent(PlayerEvent.PlayerLoggedInEvent event) {
        MessageRequest mrq = new MessageRequest();
        mrq.send(ClientConnectedToServerCmd, new Object[0]);
    }
}