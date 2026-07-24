package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.Display;

public class ReplyOpScreenShot extends Reply {
    public static final int SMID = 2305;

    public void handler(String path) {
        try {
            BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(Display.getX(), Display.getY(), Display.getWidth(), Display.getHeight()));
            File file = new File(path);
            ImageIO.write(screenshot, "png", file);
            MessageRequest mrq = new MessageRequest();
            mrq.send(SMID, new Object[]{(byte) 0, Short.valueOf(GameState.gameid), file.getAbsolutePath()});
        } catch (Exception e) {
            Common.Log(e.getMessage());
            MessageRequest mrq2 = new MessageRequest();
            mrq2.send(SMID, new Object[]{(byte) 1, Short.valueOf(GameState.gameid), ""});
        }
    }
}