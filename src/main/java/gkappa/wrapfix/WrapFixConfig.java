package gkappa.wrapfix;

import net.minecraftforge.common.config.Config;

@Config(modid = WrapFix.MODID, name = WrapFix.MODID)
public class WrapFixConfig {
    @Config.Comment("Do not set to true on Cleanroom")
    public static boolean patchVanilla = true;
    public static boolean patchIndustrialWires = true;
    public static boolean patchBetterQuesting = true;
    public static boolean patchBotania = true;
    public static boolean patchPsi = true;

}
