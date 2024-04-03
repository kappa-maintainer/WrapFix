package gkappa.wrapfix;

import com.ibm.icu.text.BreakIterator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = WrapFix.MODID, name = WrapFix.NAME, version = WrapFix.VERSION, dependencies = "required-after:mixinbooter@[8.0,);required-after:configanytime")
public class WrapFix {
    public static final String MODID = "wrapfix";
    public static final String NAME = "WrapFix";
    public static final String VERSION = "1.3.3-beta";

    public static final BreakIterator BREAK_ITERATOR = BreakIterator.getLineInstance();
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();

    }

}