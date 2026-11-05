package com.netease.mc.mod.mixin.netgame;

import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.netease.mc.mod.authlib.authlibWrapper;
import java.net.InetAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = {"net.minecraft.server.network.ServerLoginPacketListenerImpl$1"})
public abstract class MixinServerLoginPacketListenerImpl {
    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;hasJoinedServer(Ljava/lang/String;Ljava/lang/String;Ljava/net/InetAddress;)Lcom/mojang/authlib/yggdrasil/ProfileResult;"), require = 1, expect = 1, allow = 1)
    private ProfileResult netease$verifyViaLauncher(MinecraftSessionService service, String username, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        if (service instanceof YggdrasilMinecraftSessionService) {
            YggdrasilMinecraftSessionService ygg = (YggdrasilMinecraftSessionService) service;
            return authlibWrapper.hasJoinedServerWrapper(ygg, username, serverId, address);
        }
        return service.hasJoinedServer(username, serverId, address);
    }
}