package gkappa.wrapfix.mixin;


import com.google.common.collect.Lists;
import gkappa.wrapfix.WrapFix;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.GuiUtilRenderComponents.removeTextColorsIfConfigured;

@Mixin({GuiUtilRenderComponents.class})
public class MixinGuiUtilRenderComponents {
    @Inject(at = @At("HEAD"), method = "splitText", cancellable = true)
    private static void modifySplitText(ITextComponent textComponent, int maxTextLength, FontRenderer fontRendererIn, boolean notPreserveSpace, boolean forceTextColor, CallbackInfoReturnable<List<ITextComponent>> cir) {
        int i = 0;
        ITextComponent itextcomponent = new TextComponentString("");
        List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
        List<ITextComponent> list1 = Lists.newArrayList(textComponent);

        for (int j = 0; j < list1.size(); ++j)
        {
            ITextComponent itextcomponent1 = list1.get(j);
            String s = itextcomponent1.getUnformattedComponentText();
            boolean flag = false;

            if (s.contains("\n"))
            {
                int k = s.indexOf(10);
                String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                ITextComponent itextcomponent2 = new TextComponentString(s1);
                itextcomponent2.setStyle(itextcomponent1.getStyle().createShallowCopy());
                list1.add(j + 1, itextcomponent2);
                flag = true;
            }

            String s4 = removeTextColorsIfConfigured(itextcomponent1.getStyle().getFormattingCode() + s, forceTextColor);
            String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = fontRendererIn.getStringWidth(s5);
            TextComponentString textcomponentstring = new TextComponentString(s5);
            textcomponentstring.setStyle(itextcomponent1.getStyle().createShallowCopy());

            if (i + i1 > maxTextLength)
            {
                WrapFix.BREAK_ITERATOR.setText(s4);
                String s2 = fontRendererIn.trimStringToWidth(s4, maxTextLength - i, false);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null)
                {
                    WrapFix.BREAK_ITERATOR.following(s2.length());
                    int l = WrapFix.BREAK_ITERATOR.previous();

                    if (l >= 0 && fontRendererIn.getStringWidth(s4.substring(0, l)) > 0)
                    {
                        s2 = s4.substring(0, l);

                        if (s4.charAt(l) == ' ' && notPreserveSpace)
                        {
                            ++l;
                        }

                        s3 = s4.substring(l);
                    }
                    else if (i > 0 && !s4.contains(" "))
                    {
                        s2 = "";
                        s3 = s4;
                    }

                    s3 = FontRenderer.getFormatFromString(s2) + s3; //Forge: Fix chat formatting not surviving line wrapping.

                    TextComponentString textcomponentstring1 = new TextComponentString(s3);
                    textcomponentstring1.setStyle(itextcomponent1.getStyle().createShallowCopy());
                    list1.add(j + 1, textcomponentstring1);
                }

                i1 = fontRendererIn.getStringWidth(s2);
                textcomponentstring = new TextComponentString(s2);
                textcomponentstring.setStyle(itextcomponent1.getStyle().createShallowCopy());
                flag = true;
            }

            if (i + i1 <= maxTextLength)
            {
                i += i1;
                itextcomponent.appendSibling(textcomponentstring);
            }
            else
            {
                flag = true;
            }

            if (flag)
            {
                list.add(itextcomponent);
                i = 0;
                itextcomponent = new TextComponentString("");
            }
        }

        list.add(itextcomponent);
        cir.setReturnValue(list);
    }

}
