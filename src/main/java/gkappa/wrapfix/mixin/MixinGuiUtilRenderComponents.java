package gkappa.wrapfix.mixin;


import com.google.common.collect.Lists;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.text.ITextComponent;
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
    private static void modifySplitText(ITextComponent textComponent, int maxTextLenght, FontRenderer fontRendererIn, boolean notPreserveSpace, boolean forceTextColor, CallbackInfoReturnable<List<ITextComponent>> cir) {
        int i = 0;
        ITextComponent itextcomponent = new TextComponentString("");
        List<ITextComponent> result = Lists.newArrayList();
        List<ITextComponent> sourceList = Lists.newArrayList(textComponent);

        for (int j = 0; j < sourceList.size(); ++j)
        {
            ITextComponent currentComponent = sourceList.get(j);
            String s = currentComponent.getUnformattedComponentText();
            boolean flag = false;

            if (s.contains("\n"))
            {
                int k = s.indexOf('\n');
                String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                ITextComponent afterNewLine = new TextComponentString(s1);
                afterNewLine.setStyle(currentComponent.getStyle().createShallowCopy());
                sourceList.add(j + 1, afterNewLine);
                flag = true;
            }

            String formattedString = removeTextColorsIfConfigured(currentComponent.getStyle().getFormattingCode() + s, forceTextColor);
            String formattedNoBreakString = formattedString.endsWith("\n") ? formattedString.substring(0, formattedString.length() - 1) : formattedString;
            int currentCLenght = fontRendererIn.getStringWidth(formattedNoBreakString);
            TextComponentString formattedComponent = new TextComponentString(formattedNoBreakString);
            formattedComponent.setStyle(currentComponent.getStyle().createShallowCopy());

            WrapFix.BREAK_ITERATOR.setText(formattedString);

            if (i + currentCLenght > maxTextLenght)
            {
                String wrappedString = fontRendererIn.trimStringToWidth(formattedString, maxTextLenght - i, false);
                String remainString = wrappedString.length() < formattedString.length() ? formattedString.substring(wrappedString.length()) : null;

                if (remainString != null) // Handle remain part
                {
                    WrapFix.BREAK_ITERATOR.following(wrappedString.length());

                    int l = WrapFix.BREAK_ITERATOR.previous();

                    if (l >= 0 && fontRendererIn.getStringWidth(formattedString.substring(0, l)) > 0)
                    {
                        wrappedString = formattedString.substring(0, l);

                        if (notPreserveSpace && formattedString.charAt(0) == ' ')
                        {
                            ++l;
                        }

                        remainString = formattedString.substring(l);
                    }
                    else if (i > 0 && !formattedString.contains(" "))
                    {
                        wrappedString = "";
                        remainString = formattedString;
                    }

                    remainString = FontRenderer.getFormatFromString(wrappedString) + remainString; //Forge: Fix chat formatting not surviving line wrapping.

                    TextComponentString finalString = new TextComponentString(remainString);
                    finalString.setStyle(currentComponent.getStyle().createShallowCopy());
                    sourceList.add(j + 1, finalString);
                }

                currentCLenght = fontRendererIn.getStringWidth(wrappedString);
                formattedComponent = new TextComponentString(wrappedString);
                formattedComponent.setStyle(currentComponent.getStyle().createShallowCopy());
                flag = true;
            }

            if (i + currentCLenght <= maxTextLenght)
            {
                i += currentCLenght;
                itextcomponent.appendSibling(formattedComponent);
            }
            else
            {
                flag = true;
            }

            if (flag)
            {
                result.add(itextcomponent);
                i = 0;
                itextcomponent = new TextComponentString("");
            }
        }

        result.add(itextcomponent);
        cir.setReturnValue(result);
    }

}
