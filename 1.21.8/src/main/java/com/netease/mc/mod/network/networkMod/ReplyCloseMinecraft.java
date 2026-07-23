package com.netease.mc.mod.network.networkMod;

import com.netease.mc.mod.Config;
import com.netease.mc.mod.network.message.reply.Reply;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;

public class ReplyCloseMinecraft extends Reply {
    public static final int SMID = 1;

    public void handler() {
        LogManager.getLogger().info("CloseGame!!!!");
        if (Config.disableAutoCloseGame) {

            Minecraft minecraft = Minecraft.getInstance();
            minecraft.execute(() -> {
                if (minecraft.player == null)
                    return;
                minecraft.player.displayClientMessage(Component.literal("接收到启动器关闭游戏请求!").withStyle(ChatFormatting.RED), false);
            });

        } else {
            Minecraft.getInstance().close();
        }
    }
}