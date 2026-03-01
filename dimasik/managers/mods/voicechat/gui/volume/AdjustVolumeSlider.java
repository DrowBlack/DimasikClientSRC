package dimasik.managers.mods.voicechat.gui.volume;

import dimasik.managers.mods.voicechat.gui.widgets.DebouncedSlider;
import lombok.Generated;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class AdjustVolumeSlider
extends DebouncedSlider {
    protected static final ITextComponent MUTED = new TranslationTextComponent("message.voicechat.muted");
    protected static final float MAXIMUM = 4.0f;
    int height;
    protected final VolumeConfigEntry volumeConfigEntry;

    public AdjustVolumeSlider(int xIn, int yIn, int widthIn, int heightIn, VolumeConfigEntry volumeConfigEntry) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(""), volumeConfigEntry.get() / 4.0);
        this.volumeConfigEntry = volumeConfigEntry;
        this.updateMessage();
        this.height = heightIn;
    }

    protected void updateMessage() {
        if (this.sliderValue <= 0.0) {
            this.setMessage(MUTED);
            return;
        }
        long amp = Math.round(this.sliderValue * 4.0 * 100.0 - 100.0);
        this.setMessage(new TranslationTextComponent("message.voicechat.volume_amplification", ((float)amp > 0.0f ? "+" : "") + amp + "%"));
    }

    @Override
    public void applyDebounced() {
        this.volumeConfigEntry.save(this.sliderValue * 4.0);
    }

    @Override
    protected void func_230979_b_() {
    }

    @Override
    protected void func_230972_a_() {
    }

    @Generated
    public int getHeight() {
        return this.height;
    }

    public static interface VolumeConfigEntry {
        public void save(double var1);

        public double get();
    }
}
