package com.netease.mc.mod.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class NeteaseMixinPlugin implements IMixinConfigPlugin {
    private static final String MIXIN_PACKAGE = "com.netease.mc.mod.mixin.";
    private int gameType;

    public void onLoad(String mixinPackage) {
        this.gameType = Integer.parseInt(System.getProperty("netease.gameType", "0"));
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        List<String> mixins = new ArrayList<>();
        if (this.gameType == 2) {
            mixins.add("netgame.MixinYggdrasilNetgame");
            mixins.add("netgame.MixinClientHandshakePacketListenerImpl");
        }
        return mixins;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}