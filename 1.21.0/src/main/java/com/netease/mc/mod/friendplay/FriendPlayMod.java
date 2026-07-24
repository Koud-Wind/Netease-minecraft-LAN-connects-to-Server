package com.netease.mc.mod.friendplay;

import com.mojang.authlib.HttpAuthenticationService;
import com.netease.mc.mod.friendplay.message.reply.ReplyJoinGame;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewClient;
import com.netease.mc.mod.friendplay.message.reply.ReplyNewSingleV2;
import com.netease.mc.mod.friendplay.message.reply.ReplyOpScreenShot;
import com.netease.mc.mod.friendplay.message.reply.ReplyReconnect;
import com.netease.mc.mod.friendplay.message.reply.ReplyStartExistSingle;
import com.netease.mc.mod.network.socket.NetworkHandler;
import com.netease.mc.mod.oldInterface.IClientHandshakePacketListenerImplOld;
import com.netease.mc.mod.oldInterface.IClientPacketListenerOld;
import java.io.IOException;
import java.net.URL;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FriendPlayMod {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean IsDisconnect = false;
    private static LanGameState lanGameState;

        public static class LanGameState {
        public boolean isOnline = false;
        public GameType gameType = null;
        public boolean isCheat = false;
    }

    public static String performGetRequestWrapper(HttpAuthenticationService service, URL url, @Nullable String authentication) throws IOException {
        return null;
    }

    public static void handleLoginWrapper(IClientPacketListenerOld old, ClientboundLoginPacket packet) {
        ClientPacketListener listener = (ClientPacketListener) old;
        listener.seenInsecureChatWarning = true;
        old.handleLoginOld(packet);
    }

    public static boolean isValidPlayerNameWrapper(String name) {
        return true;
    }

    public static void handleHelloWrapper(IClientHandshakePacketListenerImplOld old, ClientboundHelloPacket packet) {
        LogManager.getLogger().info("Client handleHello packet");
        old.handleHelloOld(packet);
    }

    public static void init() {
        NetworkHandler.networkHandler.register(ReplyJoinGame.SMID, new ReplyJoinGame());
        NetworkHandler.networkHandler.register(ReplyStartExistSingle.SMID, new ReplyStartExistSingle());
        NetworkHandler.networkHandler.register(ReplyNewClient.SMID, new ReplyNewClient());
        NetworkHandler.networkHandler.register(ReplyNewSingleV2.SMID, new ReplyNewSingleV2());
        NetworkHandler.networkHandler.register(ReplyReconnect.SMID, new ReplyReconnect());
        NetworkHandler.networkHandler.registerAsync(ReplyOpScreenShot.SMID, new ReplyOpScreenShot());
        MinecraftForge.EVENT_BUS.register(new GuiOpenEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerEnterWorldEventHandler());
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
            case 0:
                return "survival";
            case 1:
                return "creative";
            case 2:
                return "hardcore";
            case ReplyJoinGame.MAX_RECOONECT /* 3 */:
                return "adventure";
            case 4:
                return "spectator";
            default:
                return "survival";
        }
    }
}