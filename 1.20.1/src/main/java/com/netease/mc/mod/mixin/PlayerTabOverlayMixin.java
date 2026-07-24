package com.netease.mc.mod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.components.PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;isEncrypted()Z"))
    private boolean isEncrypted(Connection connection) {
        return true;
    }

    @Inject(method = { "renderPingIcon"}, at = @At("HEAD"), cancellable = true)
    private void renderPingNumber(GuiGraphics p_283286_, int p_281809_, int p_282801_, int p_282223_, PlayerInfo p_282986_, CallbackInfo ci) {
        if (p_282986_ == null) return;

        int ping = p_282986_.getLatency();
        String text = ping < 0 ? "?" : String.valueOf(ping);

        int color;
        if (ping < 0) {
            color = 11141120;
        } else if (ping <= 10) {
            color = 16777215;
        } else if (ping <= 150) {
            color = 5635925;
        } else if (ping <= 300) {
            color = 16777045;
        } else {
            color = 16733525;
        }

        Font font = Minecraft.getInstance().font;

        p_283286_.drawString(font, text, p_282801_ + p_281809_ - font.width(text), p_282223_, color, true);
        ci.cancel();
    }
}
