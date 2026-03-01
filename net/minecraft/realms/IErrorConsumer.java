package net.minecraft.realms;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public interface IErrorConsumer {
    public void func_230434_a_(ITextComponent var1);

    default public void func_237703_a_(String p_237703_1_) {
        this.func_230434_a_(new StringTextComponent(p_237703_1_));
    }
}
