package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplyJoinGame extends Reply {
    public static final int SMID = 1799;
    public String lastIp;
    public int lastPort;
    public String lastRoom;
    public static final int MAX_RECOONECT = 3;
    private static final Logger LOGGER = LogManager.getLogger();
    public static long timeStamp = 0;
    public static ReplyJoinGame lastReply = null;
    private static int reconnectNum = 0;

    public void handler(String ip, int port, String room) {
        byte result;
        lastReply = this;
        this.lastIp = ip;
        this.lastPort = port;
        this.lastRoom = room;
        if (GameState.gameState == GameState.GameS.INIT) {
            result = 1;
        } else if (GameState.gameState == GameState.GameS.LOAD) {
            try {
                Minecraft mc = Minecraft.getInstance();
                LOGGER.info(String.format("ReplyJoinGame, room: %s, port: %d", room, Integer.valueOf(port)));
                ServerData serverData = new ServerData(room, ip + ":" + port, true);
                ConnectScreen.startConnecting(new TitleScreen(), mc, ServerAddress.parseString(serverData.ip), serverData, false);
                timeStamp = System.currentTimeMillis();
                result = 0;
            } catch (Exception e) {
                LOGGER.error("ReplyJoinGame", e);
                GameState.gameState = GameState.GameS.LOAD;
                result = 3;
            }
        } else {
            result = 2;
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(SMID, new Object[]{Byte.valueOf(result), Short.valueOf(GameState.gameid)});
    }

    public static void reconnect() {
        if (lastReply == null) {
            return;
        }
        reconnectNum++;
        if (reconnectNum <= 3) {
            LOGGER.info("reconnect join room:" + reconnectNum);
            lastReply.handler(lastReply.lastIp, lastReply.lastPort, lastReply.lastRoom);
        }
    }
}
