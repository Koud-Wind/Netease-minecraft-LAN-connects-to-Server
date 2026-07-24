package com.netease.mc.mod.plugin;

import java.util.Map;

import com.netease.mc.mod.skin.coremod.SkinContainer;
import com.netease.mc.mod.skin.coremod.SkinCoreTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import com.netease.mc.core.filter.FilterServerTransformer;

@IFMLLoadingPlugin.TransformerExclusions({"com.netease.mc.mod.plugin"})
@IFMLLoadingPlugin.Name("NeteaseModPlugin")
/* loaded from: YggdrasilServerPlugin.class */
public class NeteaseModPlugin implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass() {
        return new String[]{SkinCoreTransformer.class.getName(), FilterServerTransformer.class.getName(), NeteaseModTransformer.class.getName()};
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