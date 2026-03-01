package dimasik.managers.mods.voicechat.gui.widgets;

import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.text.ITextComponent;

public abstract class DebouncedSlider
extends AbstractSlider {
    private boolean dragged;
    private double lastValue;

    public DebouncedSlider(int i, int j, int k, int l, ITextComponent component, double d) {
        super(i, j, k, l, component, d);
        this.lastValue = d;
    }

    @Override
    public boolean keyPressed(int keyCode, int j, int k) {
        boolean result = super.keyPressed(keyCode, j, k);
        if (keyCode == 263 || keyCode == 262) {
            this.applyDebouncedInternal();
        }
        return result;
    }

    @Override
    public void onClick(double d, double e) {
        super.onClick(d, e);
        this.applyDebouncedInternal();
    }

    @Override
    protected void onDrag(double d, double e, double f, double g) {
        super.onDrag(d, e, f, g);
        this.dragged = true;
        if (this.sliderValue >= 1.0 || this.sliderValue <= 0.0) {
            this.applyDebouncedInternal();
            this.dragged = false;
        }
    }

    @Override
    public void onRelease(double d, double e) {
        super.onRelease(d, e);
        if (this.dragged) {
            this.applyDebouncedInternal();
            this.dragged = false;
        }
    }

    private void applyDebouncedInternal() {
        if (this.sliderValue == this.lastValue) {
            return;
        }
        this.lastValue = this.sliderValue;
        this.applyDebounced();
    }

    public abstract void applyDebounced();

    protected void applyValue() {
    }
}
