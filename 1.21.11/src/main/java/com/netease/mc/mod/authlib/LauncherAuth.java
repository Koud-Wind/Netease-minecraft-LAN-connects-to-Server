package com.netease.mc.mod.authlib;

import com.netease.mc.mod.network.common.GameState;
import java.lang.reflect.Method;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherAuth {
    private static final Logger LOGGER = LogManager.getLogger("NeteasePatch");
    private static final String SESSION_SERVICE = "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService";
    private static final String NETEASE_HANDLER_PREFIX = "netease$";
    private static volatile Boolean takeOver;

        public enum Result {
        HANDLED,
        PASS_THROUGH,
        FAILED
    }

    private LauncherAuth() {
    }

    public static Result authenticate(String serverHash) {
        if (!shouldTakeOver()) {
            return Result.PASS_THROUGH;
        }
        try {
            Minecraft mc = Minecraft.getInstance();
            UUID uuid = mc.getUser().getProfileId();
            String token = mc.getUser().getAccessToken();
            authlibWrapper.joinServerWrapper(null, uuid, token, serverHash);
            LOGGER.info("Authenticated via the NetEase launcher");
            return Result.HANDLED;
        } catch (LinkageError e) {
            takeOver = Boolean.FALSE;
            LOGGER.error("netease_official no longer provides authlibWrapper#joinServerWrapper; handing authentication back to it", e);
            return Result.PASS_THROUGH;
        } catch (Throwable t) {
            LOGGER.error("The NetEase launcher rejected the session", t);
            return Result.FAILED;
        }
    }

    private static boolean shouldTakeOver() {
        Boolean cached = takeOver;
        if (cached != null) {
            return cached.booleanValue();
        }
        try {
            if (GameState.userPropertiesEx == null || GameState.userPropertiesEx.GameType != 2) {
                LOGGER.info("Not a NetEase account (GameType != 2) - leaving session authentication alone");
                takeOver = Boolean.FALSE;
                return false;
            }
            boolean needed = true;
            try {
                Method[] declaredMethods = Class.forName(SESSION_SERVICE).getDeclaredMethods();
                int length = declaredMethods.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Method m = declaredMethods[i];
                    if (!m.getName().startsWith(NETEASE_HANDLER_PREFIX)) {
                        i++;
                    } else {
                        needed = false;
                        break;
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Could not inspect {}; assuming NetEase's hook is inactive and taking over", SESSION_SERVICE, t);
            }
            if (needed) {
                LOGGER.info("NetEase's joinServer hook is not applied - taking over session authentication");
            } else {
                LOGGER.info("NetEase's joinServer hook is active - leaving session authentication alone");
            }
            takeOver = Boolean.valueOf(needed);
            return needed;
        } catch (Throwable t2) {
            LOGGER.warn("Could not inspect GameState - not taking over session authentication", t2);
            takeOver = Boolean.FALSE;
            return false;
        }
    }
}