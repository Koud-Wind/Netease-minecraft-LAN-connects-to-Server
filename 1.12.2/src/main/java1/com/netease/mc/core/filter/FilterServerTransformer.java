//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.netease.mc.core.filter;

import com.netease.mc.core.filter.BaseAsmTransformer.RegisterTransformer;
import com.netease.mc.mod.filter.FilterWrapper;
import com.netease.mc.mod.filter.old.IChunkOld;
import com.netease.mc.mod.filter.old.IEntityPlayerOld;
import com.netease.mc.mod.filter.old.IMinecraftServerOld;
import com.netease.mc.mod.filter.old.INetworkManagerOld;
import com.netease.mc.mod.filter.old.IScoreboardSaveDataOld;

public class FilterServerTransformer extends BaseAsmTransformer {
    private static final String INVOKE_TARGET_CLASS = FilterWrapper.class.getName().replace(".", "/");

    @RegisterTransformer(
            className = "net.minecraft.network.NetworkManager",
            srgName = "func_150727_a",
            mcpName = "enableEncryption",
            desc = "(Ljavax/crypto/SecretKey;)V",
            oldInterface = INetworkManagerOld.class
    )
    public static class enableEncryptionTransformer extends BaseMethodTransfer {
    }

    @RegisterTransformer(
            className = "net.minecraft.server.MinecraftServer",
            srgName = "func_175577_aI",
            mcpName = "getNetworkCompressionThreshold",
            desc = "()I",
            oldInterface = IMinecraftServerOld.class
    )
    public static class getNetworkCompressionThresholdTransformer extends BaseMethodTransfer {
    }
}
