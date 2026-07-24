package com.netease.mc.mod.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SharedConstants.class})
public abstract class MixinSharedConstants {
    @Inject(method = "isAllowedChatCharacter", at = @At("HEAD"), cancellable = true)
    private static void isAllowedChatCharacter(char p_336025_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(p_336025_ >= 32 && p_336025_ != 127);
    }
}