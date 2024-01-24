package gkappa.wrapfix.mixin;


import betterquesting.api.utils.RenderUtils;
import gkappa.wrapfix.CJKTextHelper;
import gkappa.wrapfix.Reset;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


@Mixin({RenderUtils.class})
public abstract class MixinRenderUtils {
    @Inject(at = {@At("HEAD")}, method = {"splitString"}, cancellable = true, remap = false)
    private static void splitString(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<List<String>> callback) {
        if (str.isEmpty()){
            callback.setReturnValue(Collections.singletonList(""));
            return;
        }
        String cleanStr = str.replaceAll("§.", "").replaceAll("§", "");
        WrapFix.BREAK_ITERATOR.setText(cleanStr);
        List<String> list = new ArrayList<>();
        String format = ""; // For last line's format since it should use format of previous line
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
                    strWidth += font.getCharWidth(c);
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
                strWidth = font.getCharWidth(c); // Width of first char of new line.
            }
            i++;
            j++;
        } while (i < str.length());
        list.add(format + str.substring(prevBreak));
        callback.setReturnValue(list);
    }

    @Inject(at = {@At("HEAD")}, method = {"splitStringWithoutFormat"}, cancellable = true, remap = false)
    private static void splitStringWithoutFormat(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<List<String>> callback) {
        if (str.isEmpty()){
            callback.setReturnValue(Collections.singletonList(""));
            return;
        }
        String cleanStr = str.replaceAll("§.", "").replaceAll("§", "");
        WrapFix.BREAK_ITERATOR.setText(cleanStr);
        List<String> list = new ArrayList<>();
        String format = ""; // For last line's format since it should use format of previous line
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
                    list.add(str.substring(prevBreak, i));
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
                    strWidth += font.getCharWidth(c);
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
                strWidth = font.getCharWidth(c); // Width of first char of new line.
            }
            i++;
            j++;
        } while (i < str.length());
        list.add(format + str.substring(prevBreak));
        callback.setReturnValue(list);
    }

    @Shadow(remap = false)
    private static boolean isFormatColor(char c1) {
        WrapFix.logger.error("MIXIN Failed");
        return false;
    }
}

