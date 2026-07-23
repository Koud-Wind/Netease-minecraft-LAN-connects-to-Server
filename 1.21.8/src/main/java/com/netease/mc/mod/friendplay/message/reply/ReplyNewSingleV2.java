package com.netease.mc.mod.friendplay.message.reply;

import com.netease.mc.mod.friendplay.FriendPlayMod;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.reply.Reply;
import com.netease.mc.mod.network.message.request.MessageRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplyNewSingleV2 extends Reply {
    public static final int SMID = 1540;
    ArrayList<String> enable;
    ArrayList<String> disable;
    private FeatureFlagSet featureSet;
    private Map<String, FeatureFlag> featureMap = new HashMap() { // from class: com.netease.mc.mod.friendplay.message.reply.ReplyNewSingleV2.1
        {
            put("trade_rebalance", FeatureFlags.TRADE_REBALANCE);
        }
    };
    private static final Logger LOGGER = LogManager.getLogger();
    private static ResourceKey<WorldPreset> preset = null;

    public static WorldDimensions createWorldDimensions(HolderLookup.Provider p_251732_) {
        if (preset == null) {
            return null;
        }
        return ((WorldPreset) ((Holder.Reference) p_251732_.lookupOrThrow(Registries.WORLD_PRESET).get(preset).orElseThrow()).value()).createWorldDimensions();
    }

    public void handler(byte gameType, byte onlineGameType, byte isCheat, boolean othercheat, byte isBuild, byte isBonus, byte worldType, byte isOnlineGame, String Seed, String save, String levelName, boolean update_1_21, boolean bundle, boolean tradeRebalance) {
        String type = FriendPlayMod.getGameType(gameType);
        String onlineType = FriendPlayMod.getGameType(onlineGameType);
        boolean cheat = isCheat == 1;
        boolean build = isBuild == 1;
        boolean bonus = isBonus == 1;
        boolean onlineGame = isOnlineGame == 1;
        byte result = 0;
        if (GameState.gameState == GameState.GameS.INIT) {
            result = 1;
        } else if (GameState.gameState == GameState.GameS.LOAD) {
            try {
                Minecraft mc = Minecraft.getInstance();
                Path p = mc.getLevelSource().getBaseDir().resolve(save);
                List<ForbiddenSymlinkInfo> list = mc.getLevelSource().getWorldDirValidator().validateDirectory(p, true);
                if (!list.isEmpty()) {
                    throw new IOException("Path " + save + " is not a directory");
                }
                preset = null;
                boolean ishardcore = false;
                if (gameType == 2) {
                    ishardcore = true;
                }
                GameType wType = GameType.byName(type);
                long worldSeed = new Random().nextLong();
                if (Seed != null && Seed.length() != 0) {
                    try {
                        long tmp = Long.parseLong(Seed);
                        if (tmp != 0) {
                            worldSeed = tmp;
                        }
                    } catch (NumberFormatException e) {
                        worldSeed = Seed.hashCode();
                    }
                }
                resetExperimental();
                setExperimental("update_1_21", update_1_21);
                setExperimental("bundle", bundle);
                setExperimental("trade_rebalance", tradeRebalance);
                DataPackConfig dataPackConfig = new DataPackConfig(this.enable, this.disable);
                WorldDataConfiguration worldData = new WorldDataConfiguration(dataPackConfig, this.featureSet);
                LevelSettings worldsettings = new LevelSettings(levelName, wType, ishardcore, Difficulty.NORMAL, cheat, new GameRules(worldData.enabledFeatures()), worldData);
                preset = new ResourceKey[]{WorldPresets.NORMAL, WorldPresets.FLAT, WorldPresets.LARGE_BIOMES, WorldPresets.AMPLIFIED}[worldType];
                WorldOptions options = new WorldOptions(worldSeed, build, bonus);
                mc.createWorldOpenFlows().createFreshLevel(save, worldsettings, options, ReplyNewSingleV2::createWorldDimensions, mc.screen);
                if (onlineGame) {
                    FriendPlayMod.setLanGameStates(onlineGame, GameType.byName(onlineType), othercheat);
                    return;
                }
                GameState.gameState = GameState.GameS.SINGLE;
            } catch (IOException e2) {
                GameState.gameState = GameState.GameS.LOAD;
                result = 4;
                LOGGER.error("ReplyNewSingleV2", e2);
            } catch (Exception e3) {
                LOGGER.error("ReplyNewSingleV2", e3);
                GameState.gameState = GameState.GameS.LOAD;
                result = 3;
            }
        } else {
            result = 2;
        }
        MessageRequest mrq = new MessageRequest();
        mrq.send(SMID, new Object[]{Short.valueOf(GameState.gameid), Byte.valueOf(result), 0});
    }

    private void resetExperimental() {
        this.enable = new ArrayList<>(DataPackConfig.DEFAULT.getEnabled());
        this.disable = new ArrayList<>(DataPackConfig.DEFAULT.getDisabled());
        this.featureSet = FeatureFlags.DEFAULT_FLAGS;
    }

    private void setExperimental(String key, boolean enable) {
        this.featureMap.get(key);
        if (enable) {
            if (!this.enable.contains(key)) {
                this.enable.add(key);
            }
            this.disable.remove(key);
            this.featureSet = this.featureSet.join(FeatureFlagSet.of(this.featureMap.get(key)));
            return;
        }
        if (!this.disable.contains(key)) {
            this.disable.add(key);
        }
        this.enable.remove(key);
    }
}