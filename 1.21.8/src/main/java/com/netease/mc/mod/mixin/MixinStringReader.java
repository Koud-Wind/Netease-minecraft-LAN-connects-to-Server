package com.netease.mc.mod.mixin;

import com.mojang.brigadier.StringReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {StringReader.class}, remap = false)
public abstract class MixinStringReader {
    @Inject(method = "isAllowedInUnquotedString(C)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private static void allowUnicodeInUnquotedString(char c, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}