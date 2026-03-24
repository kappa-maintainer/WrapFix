package top.outlands.wrapfix;

import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class WrapFixMixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        if (s1.endsWith("MixinPageText")) return WrapFixConfig.patchBotania && Loader.isModLoaded("botania");
        if (s1.endsWith("MixinRenderUtils"))
            return WrapFixConfig.patchBetterQuesting && Loader.isModLoaded("betterquesting");
        if (s1.endsWith("MixinTextHelper")) return WrapFixConfig.patchPsi && Loader.isModLoaded("psi");
        if (s1.endsWith("MixinTextSplitter"))
            return WrapFixConfig.patchIndustrialWires && Loader.isModLoaded("industrialwires");
        return false;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
