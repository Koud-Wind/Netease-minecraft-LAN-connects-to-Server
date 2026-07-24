package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.Config;
import com.netease.mc.mod.friendplay.message.reply.ReplyJoinRoom;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerEnterWorldEventHandler {
    public static final int SMID = 1537;
    public static boolean first_enter = true;
    public static int localPort = -1;

    @SubscribeEvent
    public void PlayerEnterWorld(EntityJoinWorldEvent event) {
        byte result;
        if (first_enter) {
            if (event.getEntity() instanceof EntityPlayerSP) {
                EntityPlayerSP entity = (EntityPlayerSP) event.getEntity();
                first_enter = false;
                Common.Log("PlayerEnterWorld" + entity.getClass().getName());
                FriendPlayMod.LanGameState lgs = FriendPlayMod.getLanGameStates();
                if (lgs.isOnline) {
                    Minecraft mc = Minecraft.getMinecraft();
                    String port = mc.getIntegratedServer().shareToLAN(lgs.gameType, lgs.isCheat);
                    if (port == null) {
                        GameState.gameState = GameState.GameS.LOAD;
                        result = 3;
                        port = "0";
                    } else {
                        GameState.gameState = GameState.GameS.SERVER;
                        result = 0;
                    }

                    localPort = Config.bridgePort;
                    if (localPort > 0 && localPort < 65536) {
                        port = String.valueOf(localPort);
                    }

                    MessageRequest mrq = new MessageRequest();
                    mrq.send(1537, new Object[]{Short.valueOf(GameState.gameid), Byte.valueOf(result), Integer.valueOf(Integer.parseInt(port))});
                }
            }
        }
    }

    @SubscribeEvent
    public void ReJoinRoom(TickEvent.ClientTickEvent event) {
        if (first_enter && System.currentTimeMillis() - ReplyJoinRoom.timeStamp > 20000) {
            ReplyJoinRoom.reconnect();
        }
    }
}
