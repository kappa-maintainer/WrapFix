package gkappa.wrapfix;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public class WrapMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Lists.newArrayList("wrapfix.BQ.mixins.json", "wrapfix.IW.mixins.json", "wrapfix.Bot.mixins.json", "wrapfix.Psi.mixins.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        if(mixinConfig.equals("wrapfix.BQ.mixins.json")) {
            return Loader.isModLoaded("betterquesting");
        }
        if(mixinConfig.equals("wrapfix.IW.mixins.json")) {
            return Loader.isModLoaded("industrialwires");
        }
        if(mixinConfig.equals("wrapfix.Bot.mixins.json")) {
            return Loader.isModLoaded("botania");
        }
        if(mixinConfig.equals("wrapfix.Psi.mixins.json")) {
            return Loader.isModLoaded("psi");
        }
        return false;
    }

}

