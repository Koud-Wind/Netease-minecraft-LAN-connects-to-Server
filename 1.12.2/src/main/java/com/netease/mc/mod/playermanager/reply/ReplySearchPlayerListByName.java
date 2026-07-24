package com.netease.mc.mod.playermanager.reply;

import com.google.gson.Gson;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.logging.log4j.LogManager;

public class ReplySearchPlayerListByName extends Reply {
    public static final int SMID = 1041;
    private static final int SearchPlayersNameCmd = 1040;
    private static Gson gson = new Gson();

    public void handler(String searchName) {
        Map<String, String> matchedPlayersMap = new HashMap<>();
        Minecraft mc = Minecraft.getMinecraft();
        Collection<NetworkPlayerInfo> playerList = mc.player.connection.getPlayerInfoMap();
        for (NetworkPlayerInfo player : playerList) {
            String uuid = player.getGameProfile().getId().toString();
            String name = player.getGameProfile().getName();
            if (null != uuid && name.contains(searchName)) {
                matchedPlayersMap.put(uuid, name);
            }
        }
        String matchedPlayersStr = "";
        for (Map.Entry<String, String> entry : matchedPlayersMap.entrySet()) {
            matchedPlayersStr = matchedPlayersStr + entry.getKey() + ":" + entry.getValue() + ", ";
        }
        LogManager.getLogger().info("SearchPlayerListByName: " + searchName);
        LogManager.getLogger().info("matched players are: " + matchedPlayersStr);
        String json = gson.toJson(matchedPlayersMap);
        MessageRequest mrq = new MessageRequest();
        mrq.send(SearchPlayersNameCmd, new Object[]{json});
    }
}
