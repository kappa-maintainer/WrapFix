package gkappa.wrapfix.mixin;

import gkappa.wrapfix.CJKTextHelper;
import malte0811.industrialwires.client.manual.TextSplitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin({TextSplitter.class})
public class MixinTextSplitter {
    @Inject(at = {@At("HEAD")}, method = {"shouldSplit"}, cancellable = true, remap = false)
    private void shouldSplit(char start, char here, CallbackInfoReturnable<Byte> callback) {
        byte ret = 1;
        if (Character.isWhitespace(start) ^ Character.isWhitespace(here)) {
            ret = 2;
        }
        if (CJKTextHelper.isCharCJK(here)) {
            ret = 2;
        }
        if (here == '<') {
            ret = 2;
        }
        if (start == '<') {
            ret = 1;
            if (here == '>') {
                ret = (byte) (ret | 0x2);
            }
        }
        callback.setReturnValue(ret);
    }
}