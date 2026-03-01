package net.minecraft.entity.monster;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;

public interface IFlinging {
    public int func_230290_eL_();

    public static boolean func_234403_a_(LivingEntity p_234403_0_, LivingEntity p_234403_1_) {
        float f1 = (float)p_234403_0_.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f = !p_234403_0_.isChild() && (int)f1 > 0 ? f1 / 2.0f + (float)p_234403_0_.world.rand.nextInt((int)f1) : f1;
        boolean flag = p_234403_1_.attackEntityFrom(DamageSource.causeMobDamage(p_234403_0_), f);
        if (flag) {
            p_234403_0_.applyEnchantments(p_234403_0_, p_234403_1_);
            if (!p_234403_0_.isChild()) {
                IFlinging.func_234404_b_(p_234403_0_, p_234403_1_);
            }
        }
        return flag;
    }

    public static void func_234404_b_(LivingEntity p_234404_0_, LivingEntity p_234404_1_) {
        double d1;
        double d0 = p_234404_0_.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        double d2 = d0 - (d1 = p_234404_1_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        if (!(d2 <= 0.0)) {
            double d3 = p_234404_1_.getPosX() - p_234404_0_.getPosX();
            double d4 = p_234404_1_.getPosZ() - p_234404_0_.getPosZ();
            float f = p_234404_0_.world.rand.nextInt(21) - 10;
            double d5 = d2 * (double)(p_234404_0_.world.rand.nextFloat() * 0.5f + 0.2f);
            Vector3d vector3d = new Vector3d(d3, 0.0, d4).normalize().scale(d5).rotateYaw(f);
            double d6 = d2 * (double)p_234404_0_.world.rand.nextFloat() * 0.5;
            p_234404_1_.addVelocity(vector3d.x, d6, vector3d.z);
            p_234404_1_.velocityChanged = true;
        }
    }
}
