package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplyStartExistSingle extends Reply {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int SMID = 1538;

    public void handler(byte gameType, byte isCheat, byte isOnline, String save, String name) {
        byte result;
        if (GameState.gameState == GameState.GameS.INIT) {
            result = 1;
        } else if (GameState.gameState == GameState.GameS.LOAD) {
            try {
                String type = FriendPlayMod.getGameType(gameType);
                boolean cheat = isCheat == 1;
                boolean online = isOnline == 1;
                Minecraft mc = Minecraft.getInstance();
                Path p = mc.getLevelSource().getBaseDir().resolve(save);
                List<ForbiddenSymlinkInfo> list = mc.getLevelSource().getWorldDirValidator().validateSave(p, true);
                if (!list.isEmpty()) {
                    throw new IOException("Path " + save + " is not a directory");
                }
                if (mc.getLevelSource().levelExists(save)) {
                    mc.createWorldOpenFlows().loadLevel(mc.screen, save);
                    if (online) {
                        FriendPlayMod.setLanGameStates(online, GameType.byName(type), cheat);
                        return;
                    } else {
                        GameState.gameState = GameState.GameS.SINGLE;
                        result = 0;
                    }
                } else {
                    result = 4;
                    GameState.gameState = GameState.GameS.LOAD;
                }
            } catch (IOException e) {
                GameState.gameState = GameState.GameS.LOAD;
                result = 5;
                LOGGER.error("ReplyStartExistSingle", e);
            } catch (Exception e2) {
                LOGGER.error("ReplyStartExistSingle", e2);
                GameState.gameState = GameState.GameS.LOAD;
                result = 3;
            }
        } else {
            result = 2;
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(SMID, new Object[]{Short.valueOf(GameState.gameid), Byte.valueOf(result), 0});
    }
}
