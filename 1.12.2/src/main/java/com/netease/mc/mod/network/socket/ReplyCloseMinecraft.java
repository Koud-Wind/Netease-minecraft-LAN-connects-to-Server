package com.netease.mc.mod.network.socket;

import com.netease.mc.mod.Config;
import com.netease.mc.mod.network.message.reply.Reply;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ReplyCloseMinecraft extends Reply {
    public static final int SMID = 1;

    public void handler() {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (Config.disableAutoCloseGame) {
            minecraft.addScheduledTask(() -> {
                if (minecraft.player == null)
                    return;

                minecraft.player.sendStatusMessage(
                        new TextComponentString("接收到启动器关闭游戏请求!")
                                .setStyle(new Style().setColor(TextFormatting.RED)),
                        false
                );
            });
        } else {
            minecraft.shutdown();
        }
    }
}
