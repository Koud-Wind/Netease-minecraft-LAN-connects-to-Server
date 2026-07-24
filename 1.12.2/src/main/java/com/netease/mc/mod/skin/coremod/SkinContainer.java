package com.netease.mc.mod.skin.coremod;

import com.google.common.eventbus.EventBus;
import java.util.Arrays;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class SkinContainer extends DummyModContainer {
    public SkinContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "skincoremod";
        meta.name = "skincoremod";
        meta.version = "1.12.2";
        meta.authorList = Arrays.asList("zhikun chen");
        meta.description = "SKinmod mod provides supports in Minecraft China.";
        meta.credits = "Netease";
        meta.url = "https://mc.163.com";
    }

    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}