package net.minecraft.util.text;

import java.util.function.UnaryOperator;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public interface IFormattableTextComponent
extends ITextComponent {
    public IFormattableTextComponent setStyle(Style var1);

    default public IFormattableTextComponent appendString(String string) {
        return this.append(new StringTextComponent(string));
    }

    public IFormattableTextComponent append(ITextComponent var1);

    default public IFormattableTextComponent modifyStyle(UnaryOperator<Style> modifyFunc) {
        this.setStyle((Style)modifyFunc.apply(this.getStyle()));
        return this;
    }

    default public IFormattableTextComponent mergeStyle(Style style) {
        this.setStyle(style.mergeStyle(this.getStyle()));
        return this;
    }

    default public IFormattableTextComponent mergeStyle(TextFormatting ... formats) {
        this.setStyle(this.getStyle().createStyleFromFormattings(formats));
        return this;
    }

    default public IFormattableTextComponent mergeStyle(TextFormatting format) {
        this.setStyle(this.getStyle().applyFormatting(format));
        return this;
    }
}
