package dimasik.managers.mods.voicechat.gui.widgets;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public abstract class EnumButton<T extends Enum<T>>
extends AbstractButton {
    protected ConfigEntry<T> entry;

    public EnumButton(int xIn, int yIn, int widthIn, int heightIn, ConfigEntry<T> entry) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(""));
        this.entry = entry;
        this.updateText();
    }

    protected void updateText() {
        this.setMessage(this.getText((Enum)this.entry.get()));
    }

    protected abstract ITextComponent getText(T var1);

    protected void onUpdate(T type) {
    }

    @Override
    public void onPress() {
        Enum e = (Enum)this.entry.get();
        Enum[] values = (Enum[])e.getClass().getEnumConstants();
        Enum type = values[(e.ordinal() + 1) % values.length];
        this.entry.set(type).save();
        this.updateText();
        this.onUpdate(type);
    }
}
