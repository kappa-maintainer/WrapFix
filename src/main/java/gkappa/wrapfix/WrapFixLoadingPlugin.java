package gkappa.wrapfix;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SortingIndex(732)
@MCVersion("1.12.2")
public class WrapFixLoadingPlugin
        implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public WrapFixLoadingPlugin() {
        ConfigAnytime.register(WrapFixConfig.class);
    }
    public String[] getASMTransformerClass() {
        return new String[0];
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

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("wrapfix.mixins.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        if(mixinConfig.equals("wrapfix.mixins.json")) {
            return WrapFixConfig.patchVanilla && System.getProperty("java.version").startsWith("1.8");
        }
        return IEarlyMixinLoader.super.shouldMixinConfigQueue(mixinConfig);
    }

    @Override
    public void onMixinConfigQueued(String mixinConfig) {
        IEarlyMixinLoader.super.onMixinConfigQueued(mixinConfig);
    }
}