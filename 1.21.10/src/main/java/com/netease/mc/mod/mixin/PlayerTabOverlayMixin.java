package com.netease.mc.mod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// - modify
@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;isEncrypted()Z"))
    private boolean isEncrypted(Connection connection) {
        return true;
    }

    @Inject(method = { "renderPingIcon"}, at = @At("HEAD"), cancellable = true)
    private void renderPingNumber(GuiGraphics guiGraphics, int width, int x, int y, PlayerInfo playerInfo, CallbackInfo ci) {
        if (guiGraphics == null) return;

        int ping = playerInfo.getLatency();
        String text = ping < 0 ? "?" : String.valueOf(ping);

        int color;
        if (ping < 0) {
            color = 0xFFAA0000;
        } else if (ping <= 10) {
            color = 0xFFFFFFFF;
        } else if (ping <= 150) {
            color = 0xFF55FF55;
        } else if (ping <= 300) {
            color = 0xFFFFFF55;
        } else {
            color = 0xFFFF5555;
        }

        Font font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, text, x + width - font.width(text), y, color, true);
        ci.cancel();
    }
}
