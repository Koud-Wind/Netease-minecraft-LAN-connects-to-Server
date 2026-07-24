package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.util.Random;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class ReplyNewSingle extends Reply {
    public static final int SMID = 1537;

    public void handler(byte gameType, byte onlineGameType, byte isCheat, byte isBuild, byte isBonus, byte worldType, byte isOnlineGame, String Seed, String save, String levelName) {
        byte result;
        String type = FriendPlayMod.getGameType(gameType);
        String onlineType = FriendPlayMod.getGameType(onlineGameType);
        boolean cheat = isCheat == 1;
        boolean build = isBuild == 1;
        boolean bonus = isBonus == 1;
        boolean onlineGame = isOnlineGame == 1;
        String port = "0";
        if (GameState.gameState == GameState.GameS.INIT) {
            result = 1;
        } else if (GameState.gameState == GameState.GameS.LOAD) {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                boolean ishardcore = false;
                if (gameType == 2) {
                    ishardcore = true;
                }
                GameType wType = GameType.getByName(type);
                long worldSeed = new Random().nextLong();
                if (Seed != null && Seed.length() != 0) {
                    try {
                        long tmp = Long.parseLong(Seed);
                        if (tmp != 0) {
                            worldSeed = tmp;
                        }
                    } catch (NumberFormatException e) {
                        worldSeed = Seed.hashCode();
                    }
                }
                WorldSettings world = new WorldSettings(worldSeed, wType, build, ishardcore, WorldType.WORLD_TYPES[worldType]);
                world.setGeneratorOptions("");
                if (bonus) {
                    world.enableBonusChest();
                }
                if (cheat) {
                    world.enableCommands();
                }
                if (null == mc.loadingScreen) {
                    mc.loadingScreen = new LoadingScreenRenderer(mc);
                }
                mc.launchIntegratedServer(save, levelName, world);
                if (onlineGame) {
                    FriendPlayMod.setLanGameStates(onlineGame, GameType.getByName(onlineType), cheat);
                    return;
                } else {
                    GameState.gameState = GameState.GameS.SINGLE;
                    result = 0;
                }
            } catch (Exception e2) {
                System.out.println(e2.toString());
                GameState.gameState = GameState.GameS.LOAD;
                port = "0";
                result = 3;
            }
        } else {
            result = 2;
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(1537, new Object[]{Short.valueOf(GameState.gameid), Byte.valueOf(result), Integer.valueOf(Integer.parseInt(port))});
    }
}
