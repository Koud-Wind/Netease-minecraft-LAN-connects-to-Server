package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;

public class ReplyStartExistSingle extends Reply {
    public static final int SMID = 1538;

    public void handler(byte gameType, byte isCheat, byte isOnline, String save, String name) {
        byte result;
        String port = "0";
        if (GameState.gameState == GameState.GameS.INIT) {
            result = 1;
        } else if (GameState.gameState == GameState.GameS.LOAD) {
            try {
                String type = FriendPlayMod.getGameType(gameType);
                boolean cheat = isCheat == 1;
                boolean online = isOnline == 1;
                Minecraft mc = Minecraft.getMinecraft();
                if (null == mc.loadingScreen) {
                    mc.loadingScreen = new LoadingScreenRenderer(mc);
                }
                if (mc.getSaveLoader().canLoadWorld(save)) {
                    WorldSettings world = null;
                    ISaveHandler isavehandler = mc.getSaveLoader().getSaveLoader(save, false);
                    WorldInfo worldinfo = isavehandler.loadWorldInfo();
                    if (worldinfo == null) {
                        world = new WorldSettings(new Random().nextLong(), GameType.SURVIVAL, false, false, WorldType.DEFAULT);
                        world.setGeneratorOptions("");
                        world.enableCommands();
                    }
                    mc.launchIntegratedServer(save, name, world);
                    if (online) {
                        FriendPlayMod.setLanGameStates(online, GameType.getByName(type), cheat);
                        return;
                    } else {
                        GameState.gameState = GameState.GameS.SINGLE;
                        result = 0;
                    }
                } else {
                    result = 4;
                    GameState.gameState = GameState.GameS.LOAD;
                }
            } catch (Exception e) {
                CatchException(e);
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

    private void CatchException(Exception e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        LogManager.getLogger().info(result.toString());
    }
}
