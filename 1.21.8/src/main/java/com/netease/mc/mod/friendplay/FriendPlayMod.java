package com.netease.mc.mod.friendplay;

import com.mojang.authlib.HttpAuthenticationService;
import com.netease.mc.mod.friendplay.message.reply.ReplyJoinGame;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewClient;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewSingleV2;
import com.netease.mc.mod.friendplay.message.reply.ReplyOpScreenShot;
import com.netease.mc.mod.friendplay.message.reply.ReplyReconnect;
import com.netease.mc.mod.friendplay.message.reply.ReplyStartExistSingle;
import com.netease.mc.mod.network.socket.NetworkHandler;
import java.io.IOException;
import java.net.URL;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FriendPlayMod {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean IsDisconnect = false;
    private static LanGameState lanGameState;

    public static String performGetRequestWrapper(HttpAuthenticationService service, URL url, @Nullable String authentication) throws IOException {
        return null;
    }

    public static void handleLoginForMixin(ClientPacketListener listener, ClientboundLoginPacket packet) {
        listener.seenInsecureChatWarning = true;
    }

    public static boolean isValidPlayerNameWrapper(String name) {
        return true;
    }

    public static void handleHelloLog(ClientboundHelloPacket packet) {
        LogManager.getLogger().info("Client handleHello packet");
    }

    public static void init() {
        NetworkHandler.networkHandler.register(1799, new ReplyJoinGame());
        NetworkHandler.networkHandler.register(1538, new ReplyStartExistSingle());
        NetworkHandler.networkHandler.register(1793, new ReplyNewClient());
        NetworkHandler.networkHandler.register(1540, new ReplyNewSingleV2());
        NetworkHandler.networkHandler.register(1796, new ReplyReconnect());
        NetworkHandler.networkHandler.registerAsync(2305, new ReplyOpScreenShot());
        NeoForge.EVENT_BUS.register(new GuiOpenEventHandler());
        NeoForge.EVENT_BUS.register(new PlayerEnterWorldEventHandler());
        NeoForge.EVENT_BUS.register(new PlayerEnterWorldEventHandler());
    }

    public static LanGameState getLanGameStateObj() {
        if (lanGameState == null) {
            lanGameState = new LanGameState();
        }

        return lanGameState;
    }

    public static void setLanGameStates(boolean bonline, GameType gtype, boolean cheat) {
        getLanGameStateObj();
        lanGameState.isOnline = bonline;
        lanGameState.gameType = gtype;
        lanGameState.isCheat = cheat;
    }

    public static LanGameState getLanGameStates() {
        return getLanGameStateObj();
    }

    public static String getGameType(byte b) {
        switch (b) {
            case 0 -> {
                return "survival";
            }
            case 1 -> {
                return "creative";
            }
            case 2 -> {
                return "hardcore";
            }
            case 3 -> {
                return "adventure";
            }
            case 4 -> {
                return "spectator";
            }
            default -> {
                return "survival";
            }
        }
    }

    public static class LanGameState {
        public boolean isOnline = false;
        public GameType gameType = null;
        public boolean isCheat = false;
    }
}
