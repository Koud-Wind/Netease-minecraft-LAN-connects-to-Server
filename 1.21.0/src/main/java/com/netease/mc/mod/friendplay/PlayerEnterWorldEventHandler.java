package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.Config;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.common.GameState.GameS;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerEnterWorldEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int SMID = 1537;
    public static boolean first_enter = true;
    public static boolean enterJoinServer = false;

    public static int localPort = -1;
    @SubscribeEvent
    public void PlayerEnterWorld(EntityJoinLevelEvent event) {
        if (first_enter) {
            Entity ent = event.getEntity();
            if (ent instanceof LocalPlayer) {
                first_enter = false;
                LocalPlayer player = (LocalPlayer)ent;
                FriendPlayMod.LanGameState lgs = FriendPlayMod.getLanGameStates();
                if (lgs.isOnline) {
                    byte result = 0;
                    Minecraft mc = Minecraft.getInstance();
                    int port = 0;
                    port = HttpUtil.getAvailablePort();
                    if (port <= 0) {
                        port = 25564;
                    }

                    boolean success = mc.getSingleplayerServer().publishServer(lgs.gameType, lgs.isCheat, port);
                    if (!success) {
                        GameState.gameState = GameS.LOAD;
                        result = 3;
                    } else {
                        GameState.gameState = GameS.SERVER;
                        result = 0;
                    }

                    localPort = Config.bridgePort;
                    if (localPort > 0 && localPort < 65536) {
                        port = localPort;
                    }

                    MessageRequest mrq = new MessageRequest();
                    mrq.send(1537, new Object[]{GameState.gameid, result, port});
                }
            }

        }
    }
}
