package gkappa.wrapfix;

import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.MixinLoader;

@MixinLoader
public class WrapMixinLoader {
    public WrapMixinLoader() {
        if (Loader.isModLoaded("industrialwires")) {
            Mixins.addConfiguration("wrapfix.IW.mixins.json");
        }
        if (Loader.isModLoaded("betterquesting")) {
            Mixins.addConfiguration("wrapfix.BQ.mixins.json");
        }
    }
}

