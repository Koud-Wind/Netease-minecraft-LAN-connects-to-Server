package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.friendplay.message.reply.ReplyJoinRoom;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.socket.ClientNetworkHandler;
import com.netease.mc.mod.network.socket.ReplyCloseMinecraft;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

@Mod(modid = FriendPlayMod.MODID, version = "1.0", dependencies = "required-after:networkmod", acceptedMinecraftVersions = "[1.12.2]")
public class FriendPlayMod {
    public static final String MODID = "friendplaymod";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "com.netease.mc.mod.friendplay.FriendPlayClientProxy", serverSide = "com.netease.mc.mod.friendplay.FriendPlayCommonProxy")
    public static FriendPlayCommonProxy proxy;
    public static boolean IsDisconnect = false;
    private static LanGameState lanGameState;

        public static class LanGameState {
        public boolean isOnline = false;
        public GameType gameType = null;
        public boolean isCheat = false;
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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameState.gameState = GameState.GameS.INIT;
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        proxy.onServerStarted(event);
    }

    public static String getGameType(byte b) {
        switch (b) {
            case 0:
                return "survival";
            case ReplyCloseMinecraft.SMID /* 1 */:
                return "creative";
            case ClientNetworkHandler.SMIDLEN /* 2 */:
                return "hardcore";
            case ReplyJoinRoom.MAX_RECOONECT /* 3 */:
                return "adventure";
            case 4:
                return "spectator";
            default:
                return "survival";
        }
    }
}