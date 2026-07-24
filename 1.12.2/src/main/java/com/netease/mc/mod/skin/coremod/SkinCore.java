package com.netease.mc.mod.skin.coremod;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("")
public class SkinCore implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass() {
        return new String[]{SkinCoreTransformer.class.getName()};
    }

    public String getModContainerClass() {
        return SkinContainer.class.getName();
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}