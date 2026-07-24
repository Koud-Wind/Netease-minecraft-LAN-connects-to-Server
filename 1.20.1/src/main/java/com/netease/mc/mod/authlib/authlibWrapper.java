package com.netease.mc.mod.authlib;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import java.net.InetAddress;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class authlibWrapper {
    public static Logger logger = LogManager.getLogger();

    public static GameProfile fillGameProfileWrapper(YggdrasilMinecraftSessionService service, GameProfile profile, boolean requireSecure) {
        return profile;
    }

            public static void joinServerWrapper(YggdrasilMinecraftSessionService service, GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException, javax.naming.AuthenticationException {
        try {
            if (GameState.userPropertiesEx == null || GameState.userPropertiesEx.GameType != 2) {
                return;
            }
            logger.info("joinServerWrapper");
            AuthenticationCpp auth = new AuthenticationCpp();
            String portString = System.getProperty("launcherControlPort");
            if (portString.isEmpty()) {
                throw new javax.naming.AuthenticationException("Unavailable port");
            }
            auth.Authentication(Integer.parseInt(portString), serverId);
        } catch (Exception e) {
            logger.info("joinServerWrapper Error");
            Common.CatchException(e);
            throw new javax.naming.AuthenticationException(e.getMessage());
        }
    }

    public static GameProfile hasJoinedServerWrapper(YggdrasilMinecraftSessionService service, GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        String name = user.getName();
        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
        GameProfile result = new GameProfile(uuid, name);
        return result;
    }
}