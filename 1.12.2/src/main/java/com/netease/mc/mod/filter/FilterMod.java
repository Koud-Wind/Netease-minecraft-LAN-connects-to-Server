package com.netease.mc.mod.filter;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.netease.mc.mod.network.common.Library;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "filtermod", version = "1.0", acceptedMinecraftVersions = "[1.12.2]")
public class FilterMod {
    private static final Logger LOGGER = LogManager.getLogger();

    private static void GetModSetting() {
        LOGGER.info("GetModSetting start");
        File modSetting = new File("modsetting.cfg");
        BufferedReader bufferedreader = null;
        try {
            try {
                if (modSetting.exists() && modSetting.isFile()) {
                    bufferedreader = Files.newReader(modSetting, StandardCharsets.UTF_8);
                    StringBuffer buffer = new StringBuffer();
                    while (true) {
                        String line = bufferedreader.readLine();
                        if (line == null) {
                            break;
                        } else {
                            buffer.append(line);
                        }
                    }
                    String jsonText = buffer.toString();
                    JsonElement jsonElement = new JsonParser().parse(jsonText);
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("NetworkEncrypt") && jsonObject.get("NetworkEncrypt").isJsonPrimitive()) {
                            FilterWrapper.NetworkEncrypt = Boolean.valueOf(jsonObject.get("NetworkEncrypt").getAsBoolean());
                            LOGGER.info("GetModSetting NetworkEncrypt: " + FilterWrapper.NetworkEncrypt);
                        } else {
                            LOGGER.error("The 'NetworkEncrypt' field is missing or not a boolean.");
                        }
                        if (jsonObject.has("NetworkCompressionThreshold")) {
                            JsonElement networkCompressionElement = jsonObject.get("NetworkCompressionThreshold");
                            if (networkCompressionElement.isJsonPrimitive() && networkCompressionElement.getAsJsonPrimitive().isNumber()) {
                                FilterWrapper.NetworkCompressionThreshold = networkCompressionElement.getAsInt();
                                LOGGER.info("GetModSetting NetworkCompressionThreshold: " + FilterWrapper.NetworkCompressionThreshold);
                            } else {
                                LOGGER.error("The 'NetworkCompressionThreshold' field is not an integer.");
                            }
                        } else {
                            LOGGER.error("The 'NetworkCompressionThreshold' field is missing.");
                        }
                    } else {
                        LOGGER.error("modsetting.cfg is not a valid JSON object.");
                    }
                }
                IOUtils.closeQuietly(bufferedreader);
            } catch (Exception e) {
                LOGGER.error("CheckGamePopUp", e);
                IOUtils.closeQuietly((Reader) null);
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly((Reader) null);
            throw th;
        }
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        GetModSetting();
        MinecraftForge.EVENT_BUS.register(new FilterMod());
    }

    @SubscribeEvent
    public void onChatMessageSent(ClientChatEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> {
            if (minecraft.player == null || Library.reviewWord(event.getMessage()) <= 0)
                return;

            minecraft.player.sendStatusMessage(
                    new TextComponentString("此消息中含有敏感字!")
                            .setStyle(new Style().setColor(TextFormatting.RED)),
                    false
            );
        });
    }
}