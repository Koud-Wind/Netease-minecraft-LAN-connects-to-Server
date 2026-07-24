package com.netease.mc.mod.playermanager.reply;

import com.google.gson.Gson;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplySearchPlayerListByName extends Reply {
    public static final int SMID = 1041;
    private static final int SearchPlayersNameCmd = 1040;
    private static final Logger LOGGER = LogManager.getLogger();
    private static Gson gson = new Gson();

    public void handler(String searchName) {
        Map<String, String> matchedPlayersMap = new HashMap<>();
        Minecraft mc = Minecraft.getInstance();
        if (null == mc.player) {
            return;
        }
        Collection<PlayerInfo> playerList = mc.player.connection.getOnlinePlayers();
        for (PlayerInfo player : playerList) {
            String uuid = player.getProfile().getId().toString();
            String name = player.getProfile().getName();
            if (name.contains(searchName)) {
                matchedPlayersMap.put(uuid, name);
            }
        }
        String matchedPlayersStr = "";
        for (Map.Entry<String, String> entry : matchedPlayersMap.entrySet()) {
            matchedPlayersStr = matchedPlayersStr + entry.getKey() + ":" + entry.getValue() + ", ";
        }
        LOGGER.info("SearchPlayerListByName: " + searchName);
        LOGGER.info("matched players are: " + matchedPlayersStr);
        String json = gson.toJson(matchedPlayersMap);
        MessageRequest mrq = new MessageRequest();
        mrq.send(SearchPlayersNameCmd, new Object[]{json});
    }
}
