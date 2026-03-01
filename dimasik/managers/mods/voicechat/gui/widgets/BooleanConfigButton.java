package dimasik.managers.mods.voicechat.gui.widgets;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import java.util.function.Function;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class BooleanConfigButton
extends AbstractButton {
    protected ConfigEntry<Boolean> entry;
    protected Function<Boolean, ITextComponent> component;

    public BooleanConfigButton(int x, int y, int width, int height, ConfigEntry<Boolean> entry, Function<Boolean, ITextComponent> component) {
        super(x, y, width, height, new StringTextComponent(""));
        this.entry = entry;
        this.component = component;
        this.updateText();
    }

    private void updateText() {
        this.setMessage(this.component.apply(this.entry.get()));
    }

    @Override
    public void onPress() {
        this.entry.set(this.entry.get() == false).save();
        this.updateText();
    }
}
