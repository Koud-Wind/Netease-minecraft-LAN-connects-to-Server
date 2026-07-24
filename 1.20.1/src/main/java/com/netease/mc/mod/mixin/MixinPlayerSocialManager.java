package com.netease.mc.mod.mixin;

import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerSocialManager.class)
public abstract class MixinPlayerSocialManager {
    @Inject(method = "startOnlineMode", at = @At("HEAD"), cancellable = true)
    private void startOnlineMode(CallbackInfo ci) {
        ci.cancel();
    }
}
