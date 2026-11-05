package com.netease.mc.mod.mixin;

import com.netease.mc.mod.encryption.EncryptionEnableWrapper;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MinecraftServer.class})
public abstract class MixinMinecraftServer {
    @Inject(method = {"getCompressionThreshold"}, at = {@At("HEAD")}, cancellable = true)
    private void netease$getCompressionThreshold(CallbackInfoReturnable<Integer> cir) {
        if (EncryptionEnableWrapper.NetworkCompressionThreshold >= -1) {
            int newThreshold = EncryptionEnableWrapper.NetworkCompressionThreshold;
            LogManager.getLogger().info("getCompressionThresholdWrapper: " + newThreshold);
            cir.setReturnValue(Integer.valueOf(newThreshold));
        }
    }
}