package com.netease.mc.mod.mixin;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({StringUtil.class})
public abstract class MixinStringUtil {
    @Inject(method = "isValidPlayerName(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private static void isValidPlayerName(String name, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(name != null && name.length() <= 16);
    }
    @Inject(method = "isAllowedChatCharacter", at = @At("HEAD"), cancellable = true)
    private static void isAllowedChatCharacter(char character, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(character >= 32 && character != 127);
    }
}