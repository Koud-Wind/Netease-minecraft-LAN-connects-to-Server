package com.netease.mc.mod.mixin;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// - modify
@Mixin({StringUtil.class})
public abstract class MixinStringUtil {
    @Inject(method = "isValidPlayerName(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private static void isValidPlayerName(String name, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(name != null && name.length() <= 16);
    }
    @Inject(method = "isAllowedChatCharacter", at = @At("HEAD"), cancellable = true)
    private static void isAllowedChatCharacter(int codepoint, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(codepoint >= 32 && codepoint != 127);
    }
}