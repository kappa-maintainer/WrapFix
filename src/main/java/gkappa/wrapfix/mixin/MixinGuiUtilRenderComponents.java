package gkappa.wrapfix.mixin;


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

@Mixin({GuiUtilRenderComponents.class})
public class MixinGuiUtilRenderComponents {
    @Shadow
    public static String removeTextColorsIfConfigured(String text, boolean forceColor) {return "";}
    @Inject(at = @At("HEAD"), method = "splitText", cancellable = true)
    private static void modifySplitText(ITextComponent textComponent, int maxTextLength, FontRenderer fontRendererIn, boolean notPreserveSpace, boolean forceTextColor, CallbackInfoReturnable<List<ITextComponent>> cir) {
        List<ITextComponent> resultList = new ArrayList<>();

        for (String str : fontRendererIn.listFormattedStringToWidth(textComponent.getFormattedText(), maxTextLength)) {
            if (notPreserveSpace && str.charAt(str.length() - 1) == ' ') {
                str = str.substring(0, str.length() - 1);
            }
            str = GuiUtilRenderComponents.removeTextColorsIfConfigured(str, forceTextColor);
            resultList.add(new TextComponentString(str).setStyle(textComponent.getStyle().createShallowCopy()));
        }

        cir.setReturnValue(resultList);
    }

}
