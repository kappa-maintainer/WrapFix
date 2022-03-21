package gkappa.wrapfix.mixin;

import com.google.common.base.Joiner;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.lexicon.page.PageText;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = {PageText.class}, remap = false)
public class MixinPageText {

    @Inject(at = {@At("HEAD")}, method = {"renderText(IIIIIZILjava/lang/String;)V"}, cancellable = true, remap = false)
    private static void renderText(int x, int y, int width, int height, int paragraphSize, boolean useUnicode, int color, String unlocalizedText, CallbackInfo ci) {
        x += 2;
        y += 10;
        width -= 4;

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        boolean unicode = font.getUnicodeFlag();
        if(useUnicode)
            font.setUnicodeFlag(true);
        String text = I18n.format(unlocalizedText).replaceAll("&", "\u00a7");
        String[] textEntries = text.split("<br>");

        List<List<String>> lines = new ArrayList<>();

        String controlCodes;
        for(String s : textEntries) {
            List<String> words = new ArrayList<>();
            String lineStr = "";
            String[] tokens = Splitter(s);

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

        int i = 0;
        for(List<String> words : lines) {
            int xi = x;
            int spacing = 4;
            int wcount = words.size();
            int compensationSpaces = 0;
            boolean justify = ConfigHandler.lexiconJustifiedText && wcount > 0 && lines.size() > i && !lines.get(i + 1).isEmpty();

            if(justify) {
                String s = Joiner.on("").join(words);
                int swidth = font.getStringWidth(s);
                int space = width - swidth;

                spacing = wcount == 1 ? 0 : space / (wcount - 1);
                compensationSpaces = wcount == 1 ? 0 : space % (wcount - 1);
            }

            for(String s : words) {
                int extra = 0;
                if(compensationSpaces > 0) {
                    compensationSpaces--;
                    extra++;
                }
                font.drawString(s, xi, y, color);
                if (s.length() == 3) {
                    if (Character.UnicodeScript.of(s.charAt(2)) == Character.UnicodeScript.HAN) {
                        spacing = 0;
                        extra =0;
                    }
                }
                xi += font.getStringWidth(s) + spacing + extra;
            }

            y += words.isEmpty() ? paragraphSize : 10;
            i++;
        }

        font.setUnicodeFlag(unicode);

        ci.cancel();
    }
    @Shadow
    private static String getControlCodes(String s) {
        return null;
    }
    @Shadow
    private static String toControlCodes(String s) {
        return null;
    }

    private static String[] Splitter(String s) {
        StringBuilder word = new StringBuilder();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == ' ') {
                word.append(c);
                result.add(word.toString());
                word = new StringBuilder();
                continue;
            }
            if(Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                if (word.length() > 0) {
                    result.add(word.toString());
                    word = new StringBuilder();
                }
                word.append(c);
                result.add(word.toString());
                word = new StringBuilder();
                continue;
            }
            word.append(c);

        }

        return  result.toArray(new String[0]);
    }
}
