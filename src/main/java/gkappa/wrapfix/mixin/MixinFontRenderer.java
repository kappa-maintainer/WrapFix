package gkappa.wrapfix.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Mixin({FontRenderer.class})
public abstract class MixinFontRenderer {

    @Inject(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;setColor(FFFF)V", ordinal = 1, shift = At.Shift.BY, by = 2))
    private void hackSeg(String text, boolean shadow, CallbackInfo ci, @Local(ordinal = 0)LocalIntRef i) {
        if (text.charAt(i.get() + 1) == '§')
            i.set(i.get() - 1);
    }

    @ModifyConstant(method = "renderStringAtPos", constant = @Constant(stringValue = "0123456789abcdefklmnor"))
    private String hackConst(String origin) {
        return "0123456789abcdefklmnor§";
    }
    
    @Inject(at = @At("HEAD"), method = "listFormattedStringToWidth", cancellable = true)
    private void wrapStringToWidthICU4J(String str, int wrapWidth, CallbackInfoReturnable<List<String>> callback) {
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
                    line.delete(0, line.length());
                    line.append(format);
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
                    lineWidth += getCharWidth(current);
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
                    line.delete(0, line.length());
                    line.append(format);
                    line.append(current);
                    lineWidth = getCharWidth(current);
                } else {
                    d = icui - fed;
                    list.add(line.substring(0, d));
                    temp = line.substring(d);
                    fed += d;
                    line.delete(0, line.length());
                    line.append(map.get(icui).getRight());
                    line.append(temp);
                    lineWidth = lineWidth - map.get(icui - 1).getLeft();
                }
            }
        }
        list.add(line.toString());

        
        /*
        StringBuilder nextFormat = new StringBuilder();
        int i = 0, j = 0, k, l;
        Stack<Reset> lastReset = new Stack<>();
        lastReset.push(new Reset(0, 0));
        int strWidth = 0;
        int prevBreak = 0;
        boolean bold = false;
        char c, f, back;
        int prevCandidate;
        do {
            switch (c = str.charAt(i)) {
                case '\n':
                    list.add(nextFormat.substring(lastReset.peek().getRight()) + str.substring(prevBreak, i));
                    format = nextFormat.toString();
                    prevBreak = i + 1;
                    strWidth = 0;
                    break;
                case '§': // format start
                    if (i + 1 < str.length()) { // Prevent out of bound
                        f = str.charAt(++i);
                        nextFormat.append('§').append(f); // Add to current format code
                        if (f != 'l' && f != 'L') { // Check start of bold style
                            if (f == 'r' || f == 'R' || isFormatColor(f)) { // Not Bold, check end of style
                                bold = false;
                                lastReset.push(new Reset(i - 1, nextFormat.length())); // push reset location in str and format to stack
                            }
                        } else {
                            bold = true;
                        }
                    }
                    break;

                default:
                    strWidth += getCharWidth(c);
                    if (bold) {
                        // Bold style is fat
                        strWidth += 1;
                    }
                    break;

            }
            if (strWidth > wrapWidth) {
                WrapFix.BREAK_ITERATOR.following(j); // The legal break right after j
                prevCandidate = WrapFix.BREAK_ITERATOR.previous(); // Find the nearest legit break
                k = i;
                l = j;
                if (prevCandidate > -1) {
                    while (l > prevCandidate && k > 0) { // Backward searching to get actual format at break
                        k--;
                        l--;
                        back = str.charAt(k);
                        if (back == '§') {
                            if (k == lastReset.peek().getLeft()) {
                                lastReset.pop(); // Remove reset
                            }
                            nextFormat.delete(nextFormat.length() - 2, nextFormat.length()); // Remove format
                            l++;
                        }
                    }
                }
                if (k <= prevBreak) {
                    k = i; // Break in previous line, not usable, set it to current i
                }
                if (str.charAt(k - 1) == '§') {
                    k--; // Make sure not to break in format code. Can't figure out how to put it in while(), so
                }
                list.add(nextFormat.substring(lastReset.peek().getRight()) + str.substring(prevBreak, k));
                format = nextFormat.toString();
                if (k != i) {
                    j = prevCandidate; // k!=i means usable break point found, j should back to corresponding location
                }
                prevBreak = k;
                i = k;
                strWidth = getCharWidth(c); // Width of first char of new line.
            }
            i++;
            j++;
        } while (i < str.length());
        list.add(format + str.substring(prevBreak));*/
        //WrapFix.logger.info(list.toString());
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
