package net.minecraft.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class InstantEffect
extends Effect {
    public InstantEffect(EffectType type, int liquidColor) {
        super(type, liquidColor);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration >= 1;
    }
}
