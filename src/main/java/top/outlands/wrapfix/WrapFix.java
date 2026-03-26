package top.outlands.wrapfix;

import com.cleanroommc.client.ICU4JInstances;
import com.ibm.icu.segmenter.Segmenter;
import com.ibm.icu.text.BreakIterator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = WrapFix.MODID, name = WrapFix.NAME, version = WrapFix.VERSION, dependencies = "required-after:cleanroom@[0.5.6-alpha,)")
public class WrapFix {
    public static final String MODID = Reference.MOD_ID;
    public static final String NAME = Reference.MOD_NAME;
    public static final String VERSION = Reference.VERSION;

    public static final BreakIterator BREAK_ITERATOR = ICU4JInstances.BREAK_ITERATOR;
    public static final Segmenter SEGMENTER = ICU4JInstances.SEGMENTER;
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();

    }

}