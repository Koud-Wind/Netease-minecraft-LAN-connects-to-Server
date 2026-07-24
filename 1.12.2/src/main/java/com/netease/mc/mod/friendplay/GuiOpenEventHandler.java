package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.friendplay.message.request.MessageCmd;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;

public class GuiOpenEventHandler {
    boolean first_mainmenu = true;

    @SubscribeEvent
    public void MainMenuOpen(GuiOpenEvent event) {
        GuiScreen gui = event.getGui();
        if (null == gui) {
            Common.Log("open screen : null");
            return;
        }
        Common.Log("open screen" + gui.getClass().getName());
        if (gui instanceof GuiMainMenu) {
            if (!this.first_mainmenu) {
                return;
            }
            this.first_mainmenu = false;
            GameState.gameState = GameState.GameS.LOAD;
            MessageRequest mrq = new MessageRequest();
            mrq.send(MessageCmd.SMID_LOADCOMPLETE, new Object[]{Short.valueOf(GameState.gameid)});
            return;
        }
        if (gui instanceof GuiDisconnected) {
            FriendPlayMod.IsDisconnect = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void ActionPerformutton(GuiScreenEvent.ActionPerformedEvent event) {
        if ((event.getGui() instanceof GuiDisconnected) || (event.getGui() instanceof GuiConnecting)) {
            event.setCanceled(true);
        }
    }

    private void CatchException(Exception e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        LogManager.getLogger().info(result.toString());
    }
}