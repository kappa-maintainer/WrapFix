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
        List<ITextComponent> resultList = Lists.newArrayList();
        List<ITextComponent> sourceList = Lists.newArrayList(textComponent);

        for (int j = 0; j < sourceList.size(); ++j)
        {
            ITextComponent current = sourceList.get(j);
            String s = current.getUnformattedComponentText();

            if (s.contains("\n"))
            {
                int k = s.indexOf('\n');
                String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                ITextComponent afterNewLine = new TextComponentString(s1);
                afterNewLine.setStyle(current.getStyle().createShallowCopy());
                sourceList.add(j + 1, afterNewLine);
            }

            String formattedCurrentStr = removeTextColorsIfConfigured(current.getStyle().getFormattingCode() + s, forceTextColor);
            String formattedNoBreakString = formattedCurrentStr.endsWith("\n") ? formattedCurrentStr.substring(0, formattedCurrentStr.length() - 1) : formattedCurrentStr;
            int currentWidth = fontRendererIn.getStringWidth(formattedNoBreakString);
            TextComponentString formattedComponent = new TextComponentString(formattedNoBreakString);
            formattedComponent.setStyle(current.getStyle().createShallowCopy());

            if (currentWidth > maxTextLenght)
            {
                for (String str : fontRendererIn.listFormattedStringToWidth(formattedCurrentStr, maxTextLenght)) {
                    resultList.add(new TextComponentString(str).setStyle(current.getStyle().createShallowCopy()));
                }
            } else {
                resultList.add(new TextComponentString(formattedCurrentStr).setStyle(current.getStyle().createShallowCopy()));
            }
        }

        cir.setReturnValue(resultList);
    }

}
