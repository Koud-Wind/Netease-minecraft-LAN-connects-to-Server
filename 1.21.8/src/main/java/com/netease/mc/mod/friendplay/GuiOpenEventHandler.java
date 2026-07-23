package com.netease.mc.mod.friendplay;

import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.common.GameState.GameS;
import com.netease.mc.mod.network.message.request.MessageRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiOpenEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    long first_main_menu_time = 0L;

    @SubscribeEvent
    public void InitButtonGUI(ScreenEvent.Init.Post event) {
        Screen gui = event.getScreen();
        if (null != gui) {
            if (gui instanceof TitleScreen) {
                if (this.first_main_menu_time > 0L)
                    return;
                LOGGER.info("MainMenuOpen for the first time, request Launcher to create world");
                this.first_main_menu_time = Common.getSystemTimeStamp();
                GameState.gameState = GameS.LOAD;
                MessageRequest mrq = new MessageRequest();
                mrq.send(1281, new Object[]{GameState.gameid});
            } else if (gui instanceof DisconnectedScreen) {
                FriendPlayMod.IsDisconnect = true;
                LOGGER.info("set the first button in GuiDisconnected");
            }

        }
    }
}
