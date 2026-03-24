package top.outlands.wrapfix.mixin;


import betterquesting.api.utils.RenderUtils;
import top.outlands.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;


@Mixin({RenderUtils.class})
public abstract class MixinRenderUtils {
    @Shadow(remap = false)
    public static List<String> splitString(String str, int wrapWidth, FontRenderer font) {
        return null;
    }

    @Inject(at = {@At("HEAD")}, method = {"splitString"}, cancellable = true, remap = false)
    private static void splitString(String str, int wrapWidth, FontRenderer font, CallbackInfoReturnable<List<String>> callback) {
        if (str.isEmpty())
        {
            callback.setReturnValue(Collections.singletonList(""));
            return;
        }
        com.ibm.icu.segmenter.Segments segments = WrapFix.SEGMENTER.segment(str);
        List<String> list = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        StringBuilder seg = new StringBuilder();
        StringBuilder format = new StringBuilder();
        StringBuilder segFormat = new StringBuilder();
        int formatSize = 0;
        boolean bold  = false;
        int lineWidth = 0;
        int segmentWidth;
        out:
        for (java.util.Iterator<com.ibm.icu.segmenter.Segment> it = segments.segments().iterator(); it.hasNext(); ) {
            com.ibm.icu.segmenter.Segment segment = it.next();
            segmentWidth = 0;
            int size = segment.limit - segment.start;
            CharSequence sequence = segment.getSubSequence();
            segFormat.setLength(0);
            segFormat.append(format);
            for (int i = 0; i < size; i++) {
                char c = sequence.charAt(i);
                switch (c) {
                    case '\n':
                        line.append(seg);
                        seg.setLength(0);
                        list.add(line.toString());
                        line.setLength(0);
                        line.append(format);
                        formatSize = format.length();
                        lineWidth = 0;
                        continue out;
                    case '§':
                        if (++i < size) {
                            char next = sequence.charAt(i);
                            if (next == 'l' || next == 'L') {
                                bold = true;
                            } else if (next == 'r' || next == 'R') {
                                bold = false;
                                format.setLength(0);
                            } else if (isFormatColor(next)) {
                                bold = false;
                            }
                            format.append('§').append(next);
                            seg.append('§').append(next);
                            continue;
                        }
                    default:
                        int cWidth = font.getCharWidth(c);
                        if (bold) cWidth++;
                        if (lineWidth + cWidth > wrapWidth) {
                            if (line.length() == formatSize) {
                                line.append(seg);
                                seg.setLength(0);
                                lineWidth = 0;
                                segmentWidth = 0;
                            } else {
                                lineWidth = segmentWidth;
                                format.setLength(0);
                                format.append(segFormat);
                            }
                            list.add(line.toString());
                            line.setLength(0);
                            line.append(format);
                            formatSize = format.length();
                        }
                        seg.append(c);
                        lineWidth += cWidth;
                        segmentWidth += cWidth;
                }
            }
        }
        line.append(seg);
        if (!line.isEmpty()) {
            list.add(line.toString());
        }
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

