package net.minecraft.entity.merchant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.IReputationType;

public interface IReputationTracking {
    public void updateReputation(IReputationType var1, Entity var2);
}
