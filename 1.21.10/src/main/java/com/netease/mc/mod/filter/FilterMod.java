package com.netease.mc.mod.filter;

import com.netease.mc.mod.network.common.Library;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.common.NeoForge;

// - modify
public class FilterMod {
    public static void init() {
        NeoForge.EVENT_BUS.register(new FilterMod());
    }

    @SubscribeEvent
    public void onChatMessageSent(ClientChatEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            if (minecraft.player == null || Library.reviewWord(event.getMessage()) <= 0)
                return;

            minecraft.player.displayClientMessage(Component.literal("此消息中含有敏感字!").withStyle(ChatFormatting.RED), false);
        });
    }
}
