package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class AttackDamageEffect
extends Effect {
    protected final double bonusPerLevel;

    protected AttackDamageEffect(EffectType type, int liquidColor, double bonusPerLevel) {
        super(type, liquidColor);
        this.bonusPerLevel = bonusPerLevel;
    }

    @Override
    public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
        return this.bonusPerLevel * (double)(amplifier + 1);
    }
}
