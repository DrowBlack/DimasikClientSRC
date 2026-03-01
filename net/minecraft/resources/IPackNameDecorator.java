package net.minecraft.resources;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public interface IPackNameDecorator {
    public static final IPackNameDecorator PLAIN = IPackNameDecorator.func_232629_a_();
    public static final IPackNameDecorator BUILTIN = IPackNameDecorator.create("pack.source.builtin");
    public static final IPackNameDecorator WORLD = IPackNameDecorator.create("pack.source.world");
    public static final IPackNameDecorator SERVER = IPackNameDecorator.create("pack.source.server");

    public ITextComponent decorate(ITextComponent var1);

    public static IPackNameDecorator func_232629_a_() {
        return name -> name;
    }

    public static IPackNameDecorator create(String source) {
        TranslationTextComponent itextcomponent = new TranslationTextComponent(source);
        return name -> new TranslationTextComponent("pack.nameAndSource", name, itextcomponent).mergeStyle(TextFormatting.GRAY);
    }
}
