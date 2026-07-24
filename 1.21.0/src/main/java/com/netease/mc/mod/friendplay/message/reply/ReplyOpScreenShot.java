package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplyOpScreenShot extends Reply {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int SMID = 2305;

    public void handler(String path) {
        try {
            LOGGER.info(String.format("ReplyOpScreenShot path: ", path));
            Minecraft mc = Minecraft.getInstance();
            BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(mc.getWindow().getX(), mc.getWindow().getY(), mc.getWindow().getWidth(), mc.getWindow().getHeight()));
            File file = new File(path);
            ImageIO.write(screenshot, "png", file);
            MessageRequest mrq = new MessageRequest();
            mrq.send(SMID, new Object[]{(byte) 0, Short.valueOf(GameState.gameid), file.getAbsolutePath()});
        } catch (Exception e) {
            LOGGER.error("ReplyOpScreenShot", e);
            MessageRequest mrq2 = new MessageRequest();
            mrq2.send(SMID, new Object[]{(byte) 1, Short.valueOf(GameState.gameid), ""});
        }
    }
}