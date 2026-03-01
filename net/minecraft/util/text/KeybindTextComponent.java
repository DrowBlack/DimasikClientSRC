package net.minecraft.util.text;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;

public class KeybindTextComponent
extends TextComponent {
    private static Function<String, Supplier<ITextComponent>> displaySupplierFunction = p_193635_0_ -> () -> new StringTextComponent((String)p_193635_0_);
    private final String keybind;
    private Supplier<ITextComponent> displaySupplier;

    public KeybindTextComponent(String keybind) {
        this.keybind = keybind;
    }

    public static void func_240696_a_(Function<String, Supplier<ITextComponent>> p_240696_0_) {
        displaySupplierFunction = p_240696_0_;
    }

    private ITextComponent func_240698_i_() {
        if (this.displaySupplier == null) {
            this.displaySupplier = displaySupplierFunction.apply(this.keybind);
        }
        return this.displaySupplier.get();
    }

    @Override
    public <T> Optional<T> func_230533_b_(ITextProperties.ITextAcceptor<T> acceptor) {
        return this.func_240698_i_().getComponent(acceptor);
    }

    @Override
    public <T> Optional<T> func_230534_b_(ITextProperties.IStyledTextAcceptor<T> acceptor, Style style) {
        return this.func_240698_i_().getComponentWithStyle(acceptor, style);
    }

    @Override
    public KeybindTextComponent copyRaw() {
        return new KeybindTextComponent(this.keybind);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof KeybindTextComponent)) {
            return false;
        }
        KeybindTextComponent keybindtextcomponent = (KeybindTextComponent)p_equals_1_;
        return this.keybind.equals(keybindtextcomponent.keybind) && super.equals(p_equals_1_);
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.keybind + "', siblings=" + String.valueOf(this.siblings) + ", style=" + String.valueOf(this.getStyle()) + "}";
    }

    public String getKeybind() {
        return this.keybind;
    }
}
