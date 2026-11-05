package com.netease.mc.mod.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.netease.mc.mod.skin.SkinHandler;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SkinTextureDownloader.class})
public abstract class MixinSkinTextureDownloader {
    @Inject(method = {"processLegacySkin"}, at = {@At("HEAD")}, cancellable = true)
    private static void netease$processLegacySkin(NativeImage image, String url, CallbackInfoReturnable<NativeImage> cir) {
        NativeImage result = SkinHandler.processLegacySkinWrapper(null, image);
        cir.setReturnValue(result);
    }
}