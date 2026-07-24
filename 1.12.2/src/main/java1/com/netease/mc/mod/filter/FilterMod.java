//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.netease.mc.mod.filter;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;

@Mod(
        modid = "filtermod",
        version = "1.0",
        acceptedMinecraftVersions = "[1.12.2]"
)
public class FilterMod {
    private static final Logger LOGGER = LogManager.getLogger();

    private static void GetModSetting() {
        LOGGER.info("GetModSetting start");
        File modSetting = new File("modsetting.cfg");
        BufferedReader bufferedreader = null;

        try {
            if (modSetting.exists() && modSetting.isFile()) {
                bufferedreader = Files.newReader(modSetting, StandardCharsets.UTF_8);
                StringBuffer buffer = new StringBuffer();

                String line;
                while((line = bufferedreader.readLine()) != null) {
                    buffer.append(line);
                }

                String jsonText = buffer.toString();
                JsonElement jsonElement = (new JsonParser()).parse(jsonText);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("NetworkEncrypt") && jsonObject.get("NetworkEncrypt").isJsonPrimitive()) {
                        FilterWrapper.NetworkEncrypt = jsonObject.get("NetworkEncrypt").getAsBoolean();
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
        } catch (Exception e) {
            LOGGER.error("CheckGamePopUp", e);
        } finally {
            IOUtils.closeQuietly(bufferedreader);
        }

    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        GetModSetting();
    }
}
