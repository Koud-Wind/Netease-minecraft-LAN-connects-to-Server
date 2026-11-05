package com.netease.mc.mod.mixin;

import com.mojang.authlib.GameProfile;
import com.netease.mc.mod.skin.SkinHandler;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SkinManager.class})
public abstract class MixinSkinManager {
    @Inject(method = {"get"}, at = {@At("HEAD")}, cancellable = true)
    private void netease$getOrLoad(GameProfile profile, CallbackInfoReturnable<CompletableFuture<Optional<PlayerSkin>>> cir) {
        SkinManager self = (SkinManager)(Object) this;
        cir.setReturnValue(SkinHandler.getOrLoadWrapper(self, profile));
    }
}