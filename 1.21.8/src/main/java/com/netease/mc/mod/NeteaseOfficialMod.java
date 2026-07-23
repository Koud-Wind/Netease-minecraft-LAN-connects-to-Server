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
import java.nio.charset.StandardCharsets;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("netease_official")
public class NeteaseOfficialMod {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "netease_official";
    private Boolean needPopup;

    public NeteaseOfficialMod(IEventBus modEventBus) {
        NetworkSocketMod.init();
        FriendPlayMod.init();
        SkinMod.init();
        PlayerManagerMod.init();
        FilterMod.init();
        CheckGamePopUp();
        ResourceLocation.parse("storemod:buy");
    }

    private boolean CheckGamePopUp() {
        if (this.needPopup != null) {
            return this.needPopup;
        } else {
            File modSetting = new File("modsetting.cfg");
            BufferedReader bufferedreader = null;

            label153: {
                boolean var14;
                try {
                    if (!modSetting.exists() || !modSetting.isFile()) {
                        break label153;
                    }

                    bufferedreader = Files.newReader(modSetting, StandardCharsets.UTF_8);
                    StringBuffer buffer = new StringBuffer();

                    String line;
                    while((line = bufferedreader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String jsonText = buffer.toString();
                    JsonElement jsonElement = JsonParser.parseString(jsonText);
                    if (!jsonElement.isJsonObject()) {
                        LOGGER.error("modsetting.cfg is not a valid JSON object.");
                    } else {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("GamePopUP") && jsonObject.get("GamePopUP").isJsonPrimitive()) {
                            this.needPopup = jsonObject.get("GamePopUP").getAsBoolean();
                        } else {
                            LOGGER.error("The 'GamePopUP' field is missing or not a boolean.");
                        }

                        if (jsonObject.has("NetworkEncrypt") && jsonObject.get("NetworkEncrypt").isJsonPrimitive()) {
                            EncryptionEnableWrapper.NetworkEncrypt = jsonObject.get("NetworkEncrypt").getAsBoolean();
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
                    }

                    var14 = this.needPopup;
                } catch (Exception e) {
                    LOGGER.error("CheckGamePopUp", e);
                    break label153;
                } finally {
                    IOUtils.closeQuietly(bufferedreader);
                }

                return var14;
            }

            this.needPopup = true;
            return true;
        }
    }
}
