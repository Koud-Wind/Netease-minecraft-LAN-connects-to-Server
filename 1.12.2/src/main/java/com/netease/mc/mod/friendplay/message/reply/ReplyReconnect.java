package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ReplyReconnect extends Reply {
    public static final int SMID = 1796;

    public void handler(String ip, int port, String room, boolean isLanGame) {
        byte result;
        if (!FriendPlayMod.IsDisconnect) {
            result = 1;
        } else {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                ServerData serverData = new ServerData(room, ip + ":" + port, isLanGame);
                FMLClientHandler.instance().connectToRealmsServer(ip, port);
                mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, serverData));
                if (isLanGame) {
                    ReplyJoinRoom.lastReply = null;
                }
                result = 0;
                FriendPlayMod.IsDisconnect = false;
            } catch (Exception e) {
                System.out.println(e.toString());
                GameState.gameState = GameState.GameS.LOAD;
                result = 3;
            }
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(SMID, new Object[]{Byte.valueOf(result), Short.valueOf(GameState.gameid)});
    }
}
