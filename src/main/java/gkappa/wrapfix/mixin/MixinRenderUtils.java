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
    @Inject(at = {@At("HEAD")}, method = {"splitString"}, cancellable = true, remap = false)
    private static void splitString(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<List<String>> callback) {
        if (str.isEmpty()){
            callback.setReturnValue(Collections.singletonList(""));
            return;
        }
        WrapFix.BREAK_ITERATOR.setText(str);
        List<String> list = new ArrayList<>();
        int lineWidth = 0, fed = 0, icui, d;
        StringBuilder format = new StringBuilder(); // For next line's format since it should use format of previous line
        HashMap<Integer, Pair<Integer, String>> map = new HashMap<>();
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
                    fed += line.length() + 1;
                    line.delete(0, line.length()).append(format);
                    lineWidth = 0;
                    map.put(i, Pair.of(lineWidth, format.toString()));
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
                                map.put(i, Pair.of(lineWidth, format.toString()));
                                map.put(++i, Pair.of(lineWidth, format.toString()));
                                continue;
                            }
                            if (f >= 'k' && f <= 'o' || f >= 'K' && f <= 'O') {
                                format.append('§').append(f); // Add to current format code
                                line.append('§').append(f);
                                map.put(i, Pair.of(lineWidth, format.toString()));
                                map.put(++i, Pair.of(lineWidth, format.toString()));
                                continue;
                            }
                        } else {
                            bold = true;
                            format.append('§').append(f); // Add to current format code
                            line.append('§').append(f);
                            map.put(i, Pair.of(lineWidth, format.toString()));
                            map.put(++i, Pair.of(lineWidth, format.toString()));
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
            map.put(i, Pair.of(lineWidth, format.toString()));
            if (lineWidth > wrapWidth) {
                icui = WrapFix.BREAK_ITERATOR.preceding(i);
                if (icui <= fed) {
                    list.add(line.substring(0,line.length() - 1));
                    fed += line.length() - 1;
                    line.delete(0, line.length()).append(format).append(current);
                    lineWidth = font.getCharWidth(current);
                } else {
                    d = icui - fed;
                    list.add(line.substring(0, d));
                    temp = line.substring(d);
                    fed += d;
                    line.delete(0, line.length()).append(map.get(icui).getRight()).append(temp);
                    lineWidth = lineWidth - map.get(icui - 1).getLeft();
                }
            }
        }
        list.add(line.toString());
        callback.setReturnValue(list);
    }

    @Inject(at = {@At("HEAD")}, method = {"splitStringWithoutFormat"}, cancellable = true, remap = false)
    private static void splitStringWithoutFormat(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<List<String>> callback) {
        if (str.isEmpty()){
            callback.setReturnValue(Collections.singletonList(""));
            return;
        }
        WrapFix.BREAK_ITERATOR.setText(str);
        List<String> list = new ArrayList<>();
        int lineWidth = 0, fed = 0, icui, d;
        StringBuilder format = new StringBuilder(); // For next line's format since it should use format of previous line
        HashMap<Integer, Pair<Integer, String>> map = new HashMap<>();
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
                    fed += line.length() + 1;
                    line.delete(0, line.length()).append(format);
                    lineWidth = 0;
                    map.put(i, Pair.of(lineWidth, format.toString()));
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
                                map.put(i, Pair.of(lineWidth, format.toString()));
                                map.put(++i, Pair.of(lineWidth, format.toString()));
                                continue;
                            }
                            if (f >= 'k' && f <= 'o' || f >= 'K' && f <= 'O') {
                                format.append('§').append(f); // Add to current format code
                                line.append('§').append(f);
                                map.put(i, Pair.of(lineWidth, format.toString()));
                                map.put(++i, Pair.of(lineWidth, format.toString()));
                                continue;
                            }
                        } else {
                            bold = true;
                            format.append('§').append(f); // Add to current format code
                            line.append('§').append(f);
                            map.put(i, Pair.of(lineWidth, format.toString()));
                            map.put(++i, Pair.of(lineWidth, format.toString()));
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
            map.put(i, Pair.of(lineWidth, format.toString()));
            if (lineWidth > wrapWidth) {
                icui = WrapFix.BREAK_ITERATOR.preceding(i);
                if (icui <= fed) {
                    list.add(line.substring(0,line.length() - 1));
                    fed += line.length() - 1;
                    line.delete(0, line.length()).append(format).append(current);
                    lineWidth = font.getCharWidth(current);
                } else {
                    d = icui - fed;
                    list.add(line.substring(0, d));
                    temp = line.substring(d);
                    fed += d;
                    line.delete(0, line.length()).append(map.get(icui).getRight()).append(temp);
                    lineWidth = lineWidth - map.get(icui - 1).getLeft();
                }
            }
        }
        list.add(line.toString());
        callback.setReturnValue(list);
    }

    @Shadow(remap = false)
    private static boolean isFormatColor(char c1) {
        WrapFix.logger.error("MIXIN Failed");
        return false;
    }
}

