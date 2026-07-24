package com.netease.mc.mod.filter;

import com.netease.mc.mod.network.common.Library;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FilterMod {
    public static void init() {
        MinecraftForge.EVENT_BUS.register(new FilterMod());
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
