package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

public class ReplyJoinRoom extends Reply {
    public static final int SMID = 1794;
    private static final String LOCALHOST = "127.0.0.1";
    public int lastPort;
    public String lastRoom;
    public static final int MAX_RECOONECT = 3;
    public static long timeStamp = 0;
    public static ReplyJoinRoom lastReply = null;
    private static int reconnectNum = 0;

    public void handler(int port, String room) {
        byte result;
        lastReply = this;
        this.lastPort = port;
        this.lastRoom = room;
        if (GameState.gameState == GameState.GameS.INIT) {
            result = 1;
        } else if (GameState.gameState == GameState.GameS.LOAD) {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                ServerData serverData = new ServerData(room, "127.0.0.1:" + port, true);
                mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, serverData));
                timeStamp = System.currentTimeMillis();
                result = 0;
            } catch (Exception e) {
                System.out.println(e.toString());
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
            Common.Log("reconnect join room:" + reconnectNum);
            lastReply.handler(lastReply.lastPort, lastReply.lastRoom);
        }
    }
}
