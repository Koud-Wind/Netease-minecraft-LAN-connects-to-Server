package com.netease.mc.mod.mixin;

import com.netease.mc.mod.authlib.LauncherAuth;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientHandshakePacketListenerImpl.class})
public abstract class MixinClientHandshakePacketListenerImplAuth {
    @Inject(method = {"authenticateServer"}, at = {@At("HEAD")}, cancellable = true)
    private void netease$authenticateServer(String serverHash, CallbackInfoReturnable<Component> cir) {
        LauncherAuth.Result result = LauncherAuth.authenticate(serverHash);
        if (result == LauncherAuth.Result.HANDLED) {
            cir.setReturnValue(null);
        } else if (result == LauncherAuth.Result.FAILED) {
            cir.setReturnValue(Component.translatable("disconnect.loginFailedInfo", new Object[]{Component.translatable("disconnect.loginFailedInfo.invalidSession")}));
        }
    }
}