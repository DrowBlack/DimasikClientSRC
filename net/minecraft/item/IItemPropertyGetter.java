package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IItemPropertyGetter {
    public float call(ItemStack var1, @Nullable ClientWorld var2, @Nullable LivingEntity var3);
}
