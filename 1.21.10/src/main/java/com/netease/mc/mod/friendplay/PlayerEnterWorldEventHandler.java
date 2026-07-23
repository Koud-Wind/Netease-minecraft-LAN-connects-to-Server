package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.Config;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.HttpUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerEnterWorldEventHandler {
    public static final int SMID = 1537;
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean first_enter = true;
    public static boolean enterJoinServer = false;
    public static int localPort = -1;

    @SubscribeEvent
    public void PlayerEnterWorld(EntityJoinLevelEvent event) {
        byte result;
        if (!first_enter) {
            return;
        }
        if (event.getEntity() instanceof LocalPlayer entity) {
            first_enter = false;
            LocalPlayer player = entity;
            LOGGER.info("PlayerEnterWorld: " + String.valueOf(player.getName()));
            FriendPlayMod.LanGameState lgs = FriendPlayMod.getLanGameStates();
            if (lgs.isOnline) {
                Minecraft mc = Minecraft.getInstance();
                int port = HttpUtil.getAvailablePort();
                if (port <= 0) {
                    port = 25564;
                }
                boolean success = mc.getSingleplayerServer().publishServer(lgs.gameType, lgs.isCheat, port);
                if (!success) {
                    GameState.gameState = GameState.GameS.LOAD;
                    result = 3;
                } else {
                    GameState.gameState = GameState.GameS.SERVER;
                    result = 0;
                }

                // - modify
                localPort = Config.bridgePort;
                if (localPort > 0 && localPort < 65536) {
                    port = localPort;
                }

                MessageRequest mrq = new MessageRequest();
                mrq.send(SMID, new Object[]{Short.valueOf(GameState.gameid), Byte.valueOf(result), Integer.valueOf(port)});
            }
        }
    }
}
