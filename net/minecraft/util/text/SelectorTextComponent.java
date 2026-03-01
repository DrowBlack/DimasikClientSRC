package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITargetedTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorTextComponent
extends TextComponent
implements ITargetedTextComponent {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String selector;
    @Nullable
    private final EntitySelector field_197670_d;

    public SelectorTextComponent(String selectorIn) {
        this.selector = selectorIn;
        EntitySelector entityselector = null;
        try {
            EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(selectorIn));
            entityselector = entityselectorparser.parse();
        }
        catch (CommandSyntaxException commandsyntaxexception) {
            LOGGER.warn("Invalid selector component: {}", (Object)selectorIn, (Object)commandsyntaxexception.getMessage());
        }
        this.field_197670_d = entityselector;
    }

    public String getSelector() {
        return this.selector;
    }

    @Override
    public IFormattableTextComponent func_230535_a_(@Nullable CommandSource p_230535_1_, @Nullable Entity p_230535_2_, int p_230535_3_) throws CommandSyntaxException {
        return p_230535_1_ != null && this.field_197670_d != null ? EntitySelector.joinNames(this.field_197670_d.select(p_230535_1_)) : new StringTextComponent("");
    }

    @Override
    public String getUnformattedComponentText() {
        return this.selector;
    }

    @Override
    public SelectorTextComponent copyRaw() {
        return new SelectorTextComponent(this.selector);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof SelectorTextComponent)) {
            return false;
        }
        SelectorTextComponent selectortextcomponent = (SelectorTextComponent)p_equals_1_;
        return this.selector.equals(selectortextcomponent.selector) && super.equals(p_equals_1_);
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.selector + "', siblings=" + String.valueOf(this.siblings) + ", style=" + String.valueOf(this.getStyle()) + "}";
    }
}
