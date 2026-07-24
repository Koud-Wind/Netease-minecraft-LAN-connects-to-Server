package com.netease.mc.core.filter;

import com.netease.mc.core.filter.BaseAsmTransformer;
import com.netease.mc.mod.filter.FilterWrapper;
import com.netease.mc.mod.filter.old.IMinecraftServerOld;
import com.netease.mc.mod.filter.old.INetworkManagerOld;

public class FilterServerTransformer extends BaseAsmTransformer {
    private static final String INVOKE_TARGET_CLASS = FilterWrapper.class.getName().replace(".", "/");

        @BaseAsmTransformer.RegisterTransformer(className = "net.minecraft.network.NetworkManager", srgName = "func_150727_a", mcpName = "enableEncryption", desc = "(Ljavax/crypto/SecretKey;)V", oldInterface = INetworkManagerOld.class)
    public static class enableEncryptionTransformer extends BaseMethodTransfer {
    }

        @BaseAsmTransformer.RegisterTransformer(className = "net.minecraft.server.MinecraftServer", srgName = "func_175577_aI", mcpName = "getNetworkCompressionThreshold", desc = "()I", oldInterface = IMinecraftServerOld.class)
    public static class getNetworkCompressionThresholdTransformer extends BaseMethodTransfer {
    }
}