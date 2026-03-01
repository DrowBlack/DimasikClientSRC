package net.optifine.config;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;

public class SliderPercentageOptionOF
extends SliderPercentageOption {
    public SliderPercentageOptionOF(String name) {
        this(name, 0.0, 1.0, 0.0f);
    }

    public SliderPercentageOptionOF(String name, double valueMin, double valueMax, float step) {
        super(name, valueMin, valueMax, step, (Function<GameSettings, Double>)null, (BiConsumer<GameSettings, Double>)null, (BiFunction<GameSettings, SliderPercentageOption, ITextComponent>)null);
        this.getter = this::getOptionValue;
        this.setter = this::setOptionValue;
        this.getDisplayStringFunc = this::getOptionText;
    }

    public SliderPercentageOptionOF(String name, double valueMin, double valueMax, double[] stepValues) {
        super(name, valueMin, valueMax, stepValues, (Function<GameSettings, Double>)null, (BiConsumer<GameSettings, Double>)null, (BiFunction<GameSettings, SliderPercentageOption, ITextComponent>)null);
        this.getter = this::getOptionValue;
        this.setter = this::setOptionValue;
        this.getDisplayStringFunc = this::getOptionText;
    }

    private double getOptionValue(GameSettings gameSettings) {
        return gameSettings.getOptionFloatValueOF(this);
    }

    private void setOptionValue(GameSettings gameSettings, double value) {
        gameSettings.setOptionFloatValueOF(this, value);
    }

    private ITextComponent getOptionText(GameSettings gameSettings, SliderPercentageOption sliderPercentageOption) {
        return gameSettings.getKeyComponentOF(sliderPercentageOption);
    }
}
