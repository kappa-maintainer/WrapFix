package gkappa.wrapfix;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = "wrapfix", name = "WrapFix", version = "0.1", dependencies = "after:industrialwires@[1.7-36,);")
public class WrapFix {
    public static final String MODID = "wrapfix";
    public static final String NAME = "WrapFix";
    public static final String VERSION = "0.1";
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}