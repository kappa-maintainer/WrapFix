package top.outlands.wrapfix;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

import javax.annotation.Nullable;
import java.util.Map;

@MCVersion("1.12.2")
public class WrapFixLoadingPlugin implements IFMLLoadingPlugin {
    public WrapFixLoadingPlugin() {
        ConfigManager.register(WrapFixConfig.class);
    }
    public String[] getASMTransformerClass() {
        return null;
    }


    public String getModContainerClass() {
        return null;
    }


    @Nullable
    public String getSetupClass() {
        return null;
    }


    public void injectData(Map<String, Object> data) {}


    public String getAccessTransformerClass() {
        return null;
    }

}