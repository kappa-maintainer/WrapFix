package gkappa.wrapfix;

import com.ibm.icu.text.BreakIterator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = WrapFix.MODID, name = WrapFix.NAME, version = WrapFix.VERSION, dependencies = "required-after:mixinbooter@[4.2,);after:industrialwires@[1.7-36,);")
public class WrapFix {
    public static final String MODID = "wrapfix";
    public static final String NAME = "WrapFix";
    public static final String VERSION = "1.0.1-beta";

    public static final BreakIterator BREAK_ITERATOR = BreakIterator.getLineInstance();
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}