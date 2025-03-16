package gkappa.wrapfix;

import net.minecraftforge.common.config.Config;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

@Config(modid = WrapFix.MODID, name = WrapFix.MODID)
public class WrapFixConfig {
    public static boolean patchVanilla = !SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9);
    public static boolean patchIndustrialWires = true;
    public static boolean patchBetterQuesting = true;
    public static boolean patchBotania = true;
    public static boolean patchPsi = true;

}
