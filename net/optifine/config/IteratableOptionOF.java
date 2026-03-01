package net.optifine.config;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.ITextComponent;

public class IteratableOptionOF
extends IteratableOption {
    public IteratableOptionOF(String nameIn) {
        super(nameIn, null, null);
        this.setter = this::nextOptionValue;
        this.getter = this::getOptionText;
    }

    public void nextOptionValue(GameSettings gameSettings, int increment) {
        gameSettings.setOptionValueOF(this, increment);
    }

    public ITextComponent getOptionText(GameSettings gameSettings, IteratableOption option) {
        return gameSettings.getKeyComponentOF(option);
    }
}
