package com.netease.mc.mod.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.netease.mc.mod.skin.SkinHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {MinecraftSessionService.class}, remap = false)
public interface MixinMinecraftSessionService {
    @Inject(method = {"getTextures"}, at = {@At("HEAD")}, cancellable = true)
    default void netease$getTextures(GameProfile profile, CallbackInfoReturnable<MinecraftProfileTextures> cir) {
        cir.setReturnValue(SkinHandler.getTexturesWrapper((MinecraftSessionService) this, profile));
    }
}