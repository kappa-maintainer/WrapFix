package gkappa.wrapfix;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@SortingIndex(732)
@MCVersion("1.12.2")
public class WrapFixLoadingPlugin
        implements IFMLLoadingPlugin {
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


    public void injectData(Map<String, Object> data) {
        MixinBootstrap.init();
        Mixins.addConfiguration("wrapfix.mixins.json");


        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }


    public String getAccessTransformerClass() {
        return null;
    }
}