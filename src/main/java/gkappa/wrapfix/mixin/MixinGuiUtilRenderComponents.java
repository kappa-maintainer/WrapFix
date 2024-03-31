package gkappa.wrapfix.mixin;


import com.google.common.collect.Lists;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.client.gui.GuiUtilRenderComponents.removeTextColorsIfConfigured;

@Mixin({GuiUtilRenderComponents.class})
public class MixinGuiUtilRenderComponents {
    @Inject(at = @At("HEAD"), method = "splitText", cancellable = true)
    private static void modifySplitText(ITextComponent textComponent, int maxWidth, FontRenderer fontRenderer, boolean notPreserveSpace, boolean forceTextColor, CallbackInfoReturnable<List<ITextComponent>> cir) {
        int accumlateWidth = 0;
        List<ITextComponent> list = Lists.newArrayList();
        List<String> cache = Lists.newArrayList();
        List<Style> styleCache = Lists.newArrayList();
        List<Boolean> newLineCache = Lists.newArrayList();
        List<ITextComponent> originList = Lists.newArrayList(textComponent);
        int j;
        boolean wrapped;
        for (j = 0; j < originList.size(); ++j) {
            ITextComponent originLine = originList.get(j);
            String unformattedOriginLine = originLine.getUnformattedComponentText();

            if (unformattedOriginLine.contains("\n")) {
                int k = unformattedOriginLine.indexOf('\n');
                String stringAfter = unformattedOriginLine.substring(k + 1);
                unformattedOriginLine = unformattedOriginLine.substring(0, k + 1);
                ITextComponent textAfterN = new TextComponentString(stringAfter);
                textAfterN.setStyle(originLine.getStyle().createShallowCopy());
                originList.add(j + 1, textAfterN);
                wrapped = true;
            } else {
                wrapped = false;
            }
        
            String rebuiltOriginLine = removeTextColorsIfConfigured(originLine.getStyle().getFormattingCode() + unformattedOriginLine, forceTextColor);
            String cleanedLine = rebuiltOriginLine.endsWith("\n") ? rebuiltOriginLine.substring(0, rebuiltOriginLine.length() - 1) : rebuiltOriginLine;
            cache.add(cleanedLine);
            styleCache.add(originLine.getStyle());
            newLineCache.add(wrapped);
        }
        WrapFix.BREAK_ITERATOR.setText(String.join("\n", cache));
        int fed = 0;
        ITextComponent lineToAdd = new TextComponentString("");
        String cleanedLine;
        Style style;
        for (j = 0; j < cache.size(); j++) {
            cleanedLine = cache.get(j);
            style = styleCache.get(j);
            wrapped = newLineCache.get(j);
            int lineWidth = fontRenderer.getStringWidth(cleanedLine);
            TextComponentString line = new TextComponentString(cleanedLine);
            line.setStyle(style.createShallowCopy());

            if (accumlateWidth + lineWidth > maxWidth)
            {
                
                String firstHalf = fontRenderer.trimStringToWidth(cleanedLine, maxWidth - accumlateWidth, false);
                String secondHalf = firstHalf.length() < cleanedLine.length() ? cleanedLine.substring(firstHalf.length()) : null;

                if (secondHalf != null)
                {
                    int l = WrapFix.BREAK_ITERATOR.preceding(fed + firstHalf.length()) - fed;

                    if (l >= 0 && fontRenderer.getStringWidth(cleanedLine.substring(0, l)) > 0)
                    {
                        firstHalf = cleanedLine.substring(0, l);

                        if (notPreserveSpace && cleanedLine.charAt(l) == ' ')
                        {
                            ++l;
                        }

                        secondHalf = cleanedLine.substring(l);
                    }
                    else if (accumlateWidth > 0 && !cleanedLine.contains(" "))
                    {
                        firstHalf = "";
                        secondHalf = cleanedLine;
                    }

                    secondHalf = FontRenderer.getFormatFromString(firstHalf) + secondHalf; //Forge: Fix chat formatting not surviving line wrapping.
                    
                    cache.set(j, secondHalf);
                    j--;
                }

                lineWidth = fontRenderer.getStringWidth(firstHalf);
                line = new TextComponentString(firstHalf);
                line.setStyle(style.createShallowCopy());
                wrapped = true;
            }
            

            if (accumlateWidth + lineWidth <= maxWidth)
            {
                accumlateWidth += lineWidth;
                lineToAdd.appendSibling(line);
                fed += line.getText().length();
            }
            else
            {
                wrapped = true;
                WrapFix.logger.debug("RARE CONDITION");
            }

            if (wrapped)
            {
                list.add(lineToAdd);
                accumlateWidth = 0;
                lineToAdd = new TextComponentString("");
            }
        }

        list.add(lineToAdd);
        cir.setReturnValue(list);
    }

}
