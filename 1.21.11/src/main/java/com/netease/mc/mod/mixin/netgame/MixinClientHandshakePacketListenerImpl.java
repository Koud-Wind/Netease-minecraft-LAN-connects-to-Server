package com.netease.mc.mod.mixin.netgame;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientHandshakePacketListenerImpl.class})
public abstract class MixinClientHandshakePacketListenerImpl {
    @Inject(method = {"handleHello"}, at = {@At("HEAD")})
    private void netease$handleHello(ClientboundHelloPacket packet, CallbackInfo ci) {
        LogManager.getLogger().info("Client handleHello packet");
    }
}