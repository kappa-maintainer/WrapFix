package gkappa.wrapfix.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;


@Mixin({FontRenderer.class})
public abstract class MixinFontRenderer {

    @Shadow
    private static boolean isFormatSpecial(char formatChar) {
        return false;
    }

    @Inject(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 1))
    private void captureLocal(String text, boolean shadow, CallbackInfo ci, @Share("i") LocalIntRef intRef, @Local(ordinal = 0) int index) {
        intRef.set(index);
    }
    @Redirect(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 1))
    private int hackSeg(String instance, @Share("i") LocalIntRef intRef) {
        int i = intRef.get() + 1;
        if (i < instance.length()) {
            char c = instance.charAt(i);
            if (!isFormatSpecial(c) && !isFormatColor(c)) {
                return -1;
            }
        }
        return instance.length();
    }
    
    @Inject(at = @At("HEAD"), method = "listFormattedStringToWidth", cancellable = true)
    private void wrapStringToWidthICU4J(String str, int wrapWidth, CallbackInfoReturnable<List<String>> callback) {
        if (str.isEmpty()){
            callback.setReturnValue(Collections.singletonList(""));
            return;
        }
        WrapFix.BREAK_ITERATOR.setText(str);
        List<String> list = new ArrayList<>();
        int lineWidth = 0, fed = 0, icui, d, prevFormat = 0;
        StringBuilder format = new StringBuilder(); // For next line's format since it should use format of previous line
        int[] widths = new int[str.length()];
        String[] formats = new String[str.length()];
        StringBuilder line = new StringBuilder();
        String temp;
        char[] chars = str.toCharArray();
        char f;
        boolean bold = false;
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            switch (current) {
                case '\n':
                    list.add(line.toString());
                    fed++;
                    line.delete(0, line.length()).append(format);
                    lineWidth = 0;
                    widths[i - fed] = lineWidth;
                    formats[i - fed] = format.toString();
                    continue;
                case '§':
                    if (i + 1 < chars.length) { // Prevent out of bound
                        f = chars[i + 1];
                        boolean isC = isFormatColor(f);
                        if (isC || isFormatSpecial(f)) {
                            if (f != 'l' && f != 'L') { // Check start of bold style
                                if (f == 'r' || f == 'R') { // Not Bold, check end of style
                                    bold = false;
                                    format.delete(0, format.length()); // Clear the format
                                } else if (isC) {
                                    bold = false;
                                }
                            } else {
                                bold = true;
                            }
                            format.append('§').append(f); // Add to current format code
                            line.append('§').append(f);
                            widths[i - fed] = lineWidth;
                            widths[i - fed + 1] = lineWidth;
                            formats[i - fed] = format.toString();
                            formats[i - fed + 1] = format.toString();
                            i++;
                            continue;
                        }
                    }
                default:
                    line.append(current);
                    lineWidth += getCharWidth(current);
                    if (bold) {
                        lineWidth++; // Bold style is one pixel wider
                    }
                    break;
            }
            widths[i - fed] = lineWidth;
            formats[i - fed] = format.toString();
            if (lineWidth >= wrapWidth) {
                if (WrapFix.BREAK_ITERATOR.isBoundary(i)) {
                    icui = i;
                } else {
                    icui = WrapFix.BREAK_ITERATOR.preceding(i);
                }
                if (icui <= fed || i == icui) {
                    list.add(line.substring(0,line.length() - 1));
                    fed += line.length() - 1;
                    line.delete(0, line.length()).append(format).append(current);
                    prevFormat = format.length();
                    lineWidth = getCharWidth(current);
                } else {
                    d = icui - fed;
                    if (line.charAt(d + prevFormat - 1) == '§') d--;
                    list.add(line.substring(0, d + prevFormat));
                    temp = line.substring(d + prevFormat);
                    fed = icui;
                    line.delete(0, line.length()).append(formats[d]).append(temp);
                    prevFormat = formats[d].length();
                    lineWidth = lineWidth - widths[d - 1];
                }
            }
        }
        list.add(line.toString());
        callback.setReturnValue(list);
    }

    @Shadow
    public int getCharWidth(char c0) {
        return 4;
    }

    @Shadow
    private static boolean isFormatColor(char c1) {
        return false;
    }
}
