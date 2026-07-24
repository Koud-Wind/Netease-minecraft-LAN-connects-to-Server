package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.world.GameType;

public class ReplyShareToLan extends Reply {
    public static final int SMID = 1795;

    public void handler(byte gameType, byte isCheat) {
        byte result;
        String port = "0";
        if (GameState.gameState == GameState.GameS.INIT) {
            result = 1;
        } else if (GameState.gameState != GameState.GameS.LOAD && GameState.gameState == GameState.GameS.SINGLE) {
            try {
                String type = FriendPlayMod.getGameType(gameType);
                boolean cheat = isCheat == 1;
                Minecraft mc = Minecraft.getMinecraft();
                FriendPlayMod.setLanGameStates(true, GameType.getByName(type), cheat);
                port = mc.getIntegratedServer().shareToLAN(GameType.getByName(type), cheat);
                if (port == null) {
                    GameState.gameState = GameState.GameS.LOAD;
                    result = 3;
                    port = "0";
                } else {
                    GameState.gameState = GameState.GameS.SERVER;
                    result = 0;
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                GameState.gameState = GameState.GameS.LOAD;
                result = 3;
                port = "0";
            }
        } else {
            result = 2;
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(SMID, new Object[]{Short.valueOf(GameState.gameid), Byte.valueOf(result), Integer.valueOf(Integer.parseInt(port))});
    }
}
