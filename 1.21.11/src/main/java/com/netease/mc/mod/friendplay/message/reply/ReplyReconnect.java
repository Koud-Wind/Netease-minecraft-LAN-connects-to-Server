package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplyReconnect extends Reply {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int SMID = 1796;

    public void handler(String ip, int port, String room, boolean isLanGame) {
        byte result;
        if (!FriendPlayMod.IsDisconnect) {
            result = 1;
        } else {
            try {
                LOGGER.info(String.format("ReplyReconnect, room: %s, port: %d", room, Integer.valueOf(port)));
                Minecraft mc = Minecraft.getInstance();
                ServerData serverData = new ServerData(room, ip + ":" + port, isLanGame ? ServerData.Type.LAN : ServerData.Type.OTHER);
                ConnectScreen.startConnecting(new TitleScreen(), mc, ServerAddress.parseString(serverData.ip), serverData, false, (TransferState) null);
                if (isLanGame) {
                    ReplyJoinGame.lastReply = null;
                }
                result = 0;
                FriendPlayMod.IsDisconnect = false;
            } catch (Exception e) {
                LOGGER.error("ReplyReconnect", e);
                GameState.gameState = GameState.GameS.LOAD;
                result = 3;
            }
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(SMID, new Object[]{Byte.valueOf(result), Short.valueOf(GameState.gameid)});
    }
}