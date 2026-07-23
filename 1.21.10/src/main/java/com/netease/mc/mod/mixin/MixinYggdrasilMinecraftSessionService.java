package com.netease.mc.mod.mixin;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.netease.mc.mod.authlib.authlibWrapper;
import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({YggdrasilMinecraftSessionService.class})
public abstract class MixinYggdrasilMinecraftSessionService {
    @Inject(method = {"fetchProfileUncached"}, at = {@At("HEAD")}, cancellable = true, remap = false)
    private void netease$fetchProfileUncached(UUID uuid, boolean requireSecure, CallbackInfoReturnable<ProfileResult> cir) {
        cir.setReturnValue(null);
    }

            @Inject(method = {"joinServer"}, at = {@At("HEAD")}, cancellable = true, remap = false)
    private void netease$joinServer(UUID uuid, String authenticationToken, String serverId, CallbackInfo ci) throws AuthenticationException, javax.naming.AuthenticationException {
        YggdrasilMinecraftSessionService self = (YggdrasilMinecraftSessionService)(Object) this;
        authlibWrapper.joinServerWrapper(self, uuid, authenticationToken, serverId);
        ci.cancel();
    }
}