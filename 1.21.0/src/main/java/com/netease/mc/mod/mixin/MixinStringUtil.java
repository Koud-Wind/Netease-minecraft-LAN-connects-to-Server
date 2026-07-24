package com.netease.mc.mod.mixin;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({StringUtil.class})
public abstract class MixinStringUtil {
    @Inject(method = "isAllowedChatCharacter", at = @At("HEAD"), cancellable = true)
    private static void isAllowedChatCharacter(char p_336025_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(p_336025_ >= 32 && p_336025_ != 127);
    }
}