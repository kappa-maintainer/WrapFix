package gkappa.wrapfix.mixin;


import betterquesting.api.utils.RenderUtils;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Mixin({RenderUtils.class})
public abstract class MixinRenderUtils {
    @Shadow(remap = false)
    public static List<String> splitString(String str, int wrapWidth, FontRenderer font) {
        return null;
    }

    @Inject(at = {@At("HEAD")}, method = {"splitString"}, cancellable = true, remap = false)
    private static void splitString(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<List<String>> callback) {
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
                    fed = i;
                    line.delete(0, line.length()).append(format);
                    lineWidth = 0;
                    widths[i - fed] = lineWidth;
                    formats[i - fed] = format.toString();
                    continue;
                case '§':
                    if (i + 1 < chars.length) { // Prevent out of bound
                        f = chars[i + 1];
                        if (f != 'l' && f != 'L') { // Check start of bold style
                            if (f == 'r' || f == 'R' || isFormatColor(f)) { // Not Bold, check end of style
                                bold = false;
                                if (f == 'r' || f == 'R') {
                                    format.delete(0, format.length()); // Clear the format
                                } else {
                                    format.append('§').append(f); // Add to current format code
                                }
                                line.append('§').append(f);
                                widths[i - fed] = lineWidth;
                                formats[i - fed] = format.toString();
                                i++;
                                continue;
                            }
                            if (f >= 'k' && f <= 'o' || f >= 'K' && f <= 'O') {
                                format.append('§').append(f); // Add to current format code
                                line.append('§').append(f);
                                widths[i - fed] = lineWidth;
                                formats[i - fed] = format.toString();
                                continue;
                            }
                        } else {
                            bold = true;
                            format.append('§').append(f); // Add to current format code
                            line.append('§').append(f);
                            widths[i - fed] = lineWidth;
                            formats[i - fed] = format.toString();
                            continue;
                        }
                    }
                default:
                    line.append(current);
                    lineWidth += font.getCharWidth(current);
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
                    fed = i - 1;
                    line.delete(0, line.length()).append(format).append(current);
                    prevFormat = format.length();
                    lineWidth = font.getCharWidth(current);
                } else {
                    d = icui - fed;
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

    @Inject(at = {@At("HEAD")}, method = {"splitStringWithoutFormat"}, cancellable = true, remap = false)
    private static void splitStringWithoutFormat(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<List<String>> callback) {
        callback.setReturnValue(splitString(str, wrapWidth, font));
    }

    @Shadow(remap = false)
    private static boolean isFormatColor(char c1) {
        WrapFix.logger.error("MIXIN Failed");
        return false;
    }
}

