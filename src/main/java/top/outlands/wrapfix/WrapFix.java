package top.outlands.wrapfix;

import com.cleanroommc.client.BreakIteratorHolder;
import com.ibm.icu.segmenter.LocalizedSegmenter;
import com.ibm.icu.segmenter.Segmenter;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.util.ULocale;
import top.outlands.wrapfix.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = WrapFix.MODID, name = WrapFix.NAME, version = WrapFix.VERSION, dependencies = "required-after:cleanroom@[0.5.5-alpha,)")
public class WrapFix {
    public static final String MODID = "wrapfix";
    public static final String NAME = "WrapFix";
    public static final String VERSION = Reference.VERSION;

    public static final BreakIterator BREAK_ITERATOR = BreakIteratorHolder.BREAK_ITERATOR;
    public static final Segmenter SEGMENTER = LocalizedSegmenter.builder().setSegmentationType(LocalizedSegmenter.SegmentationType.LINE).setLocale(ULocale.getDefault()).build();
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();

    }

}