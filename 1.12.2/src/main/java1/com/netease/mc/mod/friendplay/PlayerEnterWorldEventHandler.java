//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.friendplay.message.reply.ReplyJoinRoom;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.common.GameState.GameS;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerEnterWorldEventHandler {
    public static final int SMID = 1537;
    public static boolean first_enter = true;

    public static int localPort = -1;
    @SubscribeEvent
    public void PlayerEnterWorld(EntityJoinWorldEvent event) {
        if (first_enter) {
            Entity ent = event.getEntity();
            if (ent instanceof EntityPlayerSP) {
                first_enter = false;
                Common.Log("PlayerEnterWorld" + ent.getClass().getName());
                EntityPlayerSP player = (EntityPlayerSP)ent;
                FriendPlayMod.LanGameState lgs = FriendPlayMod.getLanGameStates();
                if (lgs.isOnline) {
                    byte result = 0;
                    Minecraft mc = Minecraft.getMinecraft();
                    String port = mc.getIntegratedServer().shareToLAN(lgs.gameType, lgs.isCheat);
                    if (port == null) {
                        GameState.gameState = GameS.LOAD;
                        result = 3;
                        port = "0";
                    } else {
                        GameState.gameState = GameS.SERVER;
                        result = 0;
                    }

                    if (localPort > 0 && localPort < 65536)
                        port = String.valueOf(localPort);

                    MessageRequest mrq = new MessageRequest();
                    mrq.send(1537, new Object[]{GameState.gameid, result, Integer.parseInt(port)});
                }
            }

        }
    }

    @SubscribeEvent
    public void ReJoinRoom(TickEvent.ClientTickEvent event) {
        if (first_enter) {
            if (System.currentTimeMillis() - ReplyJoinRoom.timeStamp > 20000L) {
                ReplyJoinRoom.reconnect();
            }

        }
    }
}
