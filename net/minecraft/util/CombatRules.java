package net.minecraft.util;

import net.minecraft.util.math.MathHelper;

public class CombatRules {
    public static float getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute) {
        float f = 2.0f + toughnessAttribute / 4.0f;
        float f1 = MathHelper.clamp(totalArmor - damage / f, totalArmor * 0.2f, 20.0f);
        return damage * (1.0f - f1 / 25.0f);
    }

    public static float getDamageAfterMagicAbsorb(float damage, float enchantModifiers) {
        float f = MathHelper.clamp(enchantModifiers, 0.0f, 20.0f);
        return damage * (1.0f - f / 25.0f);
    }
}
