package com.netease.mc.mod.skin;

import com.netease.mc.mod.network.socket.NetworkSocketMod;
import com.netease.mc.mod.skin.message.reply.LoadSkinReply;
import com.netease.mc.mod.skin.message.reply.LoadSkinReplyV2;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = SkinMod.MODID, version = "1.0", dependencies = "required-after:networkmod", acceptedMinecraftVersions = "[1.12.2]")
@SideOnly(Side.CLIENT)
public class SkinMod {
    public static final String MODID = "skinmod";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkSocketMod.networkHandler.registerAsync(LoadSkinReply.SMID, new LoadSkinReply());
        NetworkSocketMod.networkHandler.registerAsync(LoadSkinReplyV2.SMID, new LoadSkinReplyV2());
    }
}