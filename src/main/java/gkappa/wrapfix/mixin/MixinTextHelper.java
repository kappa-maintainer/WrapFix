package gkappa.wrapfix.mixin;

import gkappa.wrapfix.StringSplitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.client.core.helper.TextHelper;

import java.util.ArrayList;
import java.util.List;


@Mixin(value = {TextHelper.class}, remap = false)
public class MixinTextHelper {

    @Shadow
    public static String getControlCodes(String s) {
        return null;
    }
    @Shadow
    public static String toControlCodes(String s) {
        return null;
    }


    @Inject(at = @At("HEAD"), method = "renderText", cancellable = true)
    private static void renderText(int x, int y, int width, String unlocalizedText, boolean centered, boolean doit, Object[] format, CallbackInfoReturnable<List<String>> cir) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        boolean unicode = font.getUnicodeFlag();
        font.setUnicodeFlag(true);
        String text = TooltipHelper.local(unlocalizedText, format).replaceAll("&", "\u00a7");

        String[] textEntries = text.split("<br>");
        List<List<String>> lines = new ArrayList<>();

        String controlCodes;
        for(String s : textEntries) {
            List<String> words = new ArrayList<>();
            String lineStr = "";
            String[] tokens = StringSplitter.Splitter(s);
            for(String token : tokens) {
                String prev = lineStr;
                lineStr += token;

                controlCodes = toControlCodes(getControlCodes(prev));
                if(font.getStringWidth(lineStr) > width) {
                    lines.add(words);
                    lineStr = controlCodes + token;
                    words = new ArrayList<>();
                }

                words.add(controlCodes + token);
            }

            if(!lineStr.isEmpty())
                lines.add(words);
            lines.add(new ArrayList<>());
        }

        List<String> textLines = new ArrayList<>();

        String lastLine = "";
        for(List<String> words : lines) {
            int xi = x;

            StringBuilder lineStr = new StringBuilder();
            for(String s : words) {

                int swidth = font.getStringWidth(s);
                if(doit) {
                    if(centered)
                        font.drawString(s, xi + width / 2 - swidth / 2, y, 0xFFFFFF);
                    else font.drawString(s, xi, y, 0xFFFFFF);
                }
                lineStr.append(s);
            }

            if((lineStr.length() > 0) || lastLine.isEmpty()) {
                y += 10;
                textLines.add(lineStr.toString());
            }
            lastLine = lineStr.toString();
        }

        font.setUnicodeFlag(unicode);
        cir.setReturnValue(textLines);
    }


}
