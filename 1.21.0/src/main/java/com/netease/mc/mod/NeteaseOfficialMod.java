package com.netease.mc.mod;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.netease.mc.mod.encryption.EncryptionEnableWrapper;
import com.netease.mc.mod.filter.FilterMod;
import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.networkMod.NetworkSocketMod;
import com.netease.mc.mod.playermanager.PlayerManagerMod;
import com.netease.mc.mod.skin.SkinMod;

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NeteaseOfficialMod.MODID)
public class NeteaseOfficialMod {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "netease_official";
    private Boolean needPopup;

    public NeteaseOfficialMod() {
        NetworkSocketMod.init();
        FriendPlayMod.init();
        SkinMod.init();
        PlayerManagerMod.init();
        FilterMod.init();
        CheckGamePopUp();
        ChannelBuilder.named(ResourceLocation.parse("storemod:buy"))
                .optional()
                .networkProtocolVersion(0)
                .simpleChannel()
                .messageBuilder(null, 99, NetworkDirection.PLAY_TO_CLIENT)
                .add();
    }

    private boolean CheckGamePopUp() {
        if (this.needPopup != null) {
            return this.needPopup.booleanValue();
        }
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
                        }
                        buffer.append(line);
                    }
                    String jsonText = buffer.toString();
                    JsonElement jsonElement = JsonParser.parseString(jsonText);
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("GamePopUP") && jsonObject.get("GamePopUP").isJsonPrimitive()) {
                            this.needPopup = Boolean.valueOf(jsonObject.get("GamePopUP").getAsBoolean());
                        } else {
                            LOGGER.error("The 'GamePopUP' field is missing or not a boolean.");
                        }
                        if (jsonObject.has("NetworkEncrypt") && jsonObject.get("NetworkEncrypt").isJsonPrimitive()) {
                            EncryptionEnableWrapper.NetworkEncrypt = Boolean.valueOf(jsonObject.get("NetworkEncrypt").getAsBoolean());
                        } else {
                            LOGGER.error("The 'NetworkEncrypt' field is missing or not a boolean.");
                        }
                        if (jsonObject.has("NetworkCompressionThreshold")) {
                            JsonElement networkCompressionElement = jsonObject.get("NetworkCompressionThreshold");
                            if (networkCompressionElement.isJsonPrimitive() && networkCompressionElement.getAsJsonPrimitive().isNumber()) {
                                EncryptionEnableWrapper.NetworkCompressionThreshold = networkCompressionElement.getAsInt();
                            } else {
                                LOGGER.error("The 'NetworkCompressionThreshold' field is not an integer.");
                            }
                        } else {
                            LOGGER.error("The 'NetworkCompressionThreshold' field is missing.");
                        }
                    } else {
                        LOGGER.error("modsetting.cfg is not a valid JSON object.");
                    }
                    boolean zBooleanValue = this.needPopup.booleanValue();
                    IOUtils.closeQuietly(bufferedreader);
                    return zBooleanValue;
                }
                IOUtils.closeQuietly((Reader) null);
            } catch (Exception e) {
                LOGGER.error("CheckGamePopUp", e);
                IOUtils.closeQuietly(bufferedreader);
            }
            this.needPopup = true;
            return true;
        } catch (Throwable th) {
            IOUtils.closeQuietly(bufferedreader);
            throw th;
        }
    }
}