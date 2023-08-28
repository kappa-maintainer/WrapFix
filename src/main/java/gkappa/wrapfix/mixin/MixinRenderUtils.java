package gkappa.wrapfix.mixin;


import betterquesting.api.utils.RenderUtils;
import gkappa.wrapfix.CJKTextHelper;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin({RenderUtils.class})
public abstract class MixinRenderUtils {
    @Inject(at = {@At("HEAD")}, method = {"sizeStringToWidth"}, cancellable = true, remap = false)
    private static void sizeStringToWidth(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<Integer> callback) {
        WrapFix.BREAK_ITERATOR.setText(str);
        int i = str.length();
        int j = 0;
        int k = 0;
        for (boolean flag = false; k < i; k++) {

            char c0 = str.charAt(k);

            switch (c0) {
                case '\n':
                    k--;
                    break;

                case '§':
                    if (k < i - 1) {
                        k++;
                        char c1 = str.charAt(k);
                        if (c1 != 'l' && c1 != 'L') {
                            if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
                                flag = false;
                            }
                            break;
                        }
                        flag = true;
                    }
                    break;

                default:
                    j += font.getCharWidth(c0);
                    if (flag) {
                        j++;
                    }
                    break;

            }
            if (c0 == '\n') {
                k++;
                break;
            }
            if (j > wrapWidth) {
                break;
            }
        }

        int temp = WrapFix.BREAK_ITERATOR.preceding(k);
        callback.setReturnValue(temp == 0 ? k : temp);
    }

    @Shadow(remap = false)
    private static boolean isFormatColor(char c1) {
        WrapFix.logger.error("MIXIN Failed");
        return false;
    }
}

