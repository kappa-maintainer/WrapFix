package gkappa.wrapfix.mixin;

import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin({FontRenderer.class})
public abstract class MixinFontRenderer {
    @Inject(at = {@At("HEAD")}, method = {"sizeStringToWidth(Ljava/lang/String;I)I"}, cancellable = true, remap = true)
    private void sizeStringToWidth(String str, int wrapWidth, CallbackInfoReturnable<Integer> callback) {
        int i = str.length();
        int j = 0;
        int k = 0;
        int l = -1;
        for (boolean flag = false; k < i; k++) {

            char c0 = str.charAt(k);

            switch (c0) {

                case '\n':
                    k--;
                    break;
                case ' ':
                    l = k;

                default:
                    if (Character.UnicodeScript.of(c0) == Character.UnicodeScript.HAN) {
                        l = k;
                    }
                    j += getCharWidth(c0);

                    if (flag) {
                        j++;
                    }
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
            }

            if (c0 == '\n') {


                l = ++k;

                break;
            }
            if (j > wrapWidth) {
                break;
            }
        }

        int temp = (k != i && l > 0 && l < k) ? l : k;

        callback.setReturnValue(Integer.valueOf(temp));
    }


    @Inject(at = {@At("HEAD")}, method = {"wrapFormattedStringToWidth"}, cancellable = false, remap = true)
    void wrapFormattedStringToWidth(String str, int wrapWidth, CallbackInfoReturnable<String> callback) {
    }


    @Shadow(remap = true)
    private int getCharWidth(char c0) {
        WrapFix.logger.error("MIXIN Failed");
        return 4;
    }

    @Shadow(remap = true)
    private static boolean isFormatColor(char c1) {
        WrapFix.logger.error("MIXIN Failed");
        return false;
    }
}
