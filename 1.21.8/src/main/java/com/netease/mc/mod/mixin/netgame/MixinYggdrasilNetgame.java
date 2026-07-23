package com.netease.mc.mod.mixin.netgame;

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.netease.mc.mod.authlib.authlibWrapper;
import java.net.InetAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {YggdrasilMinecraftSessionService.class}, remap = false)
public abstract class MixinYggdrasilNetgame {
    @Inject(method = {"hasJoinedServer"}, at = {@At("HEAD")}, cancellable = true)
    private void netease$hasJoinedServer(String username, String serverId, InetAddress address, CallbackInfoReturnable<ProfileResult> cir) {
        try {
            cir.setReturnValue(authlibWrapper.hasJoinedServerWrapper((YggdrasilMinecraftSessionService)(Object) this, username, serverId, address));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}