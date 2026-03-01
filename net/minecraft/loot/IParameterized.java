package net.minecraft.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.ValidationTracker;

public interface IParameterized {
    default public Set<LootParameter<?>> getRequiredParameters() {
        return ImmutableSet.of();
    }

    default public void func_225580_a_(ValidationTracker p_225580_1_) {
        p_225580_1_.func_227528_a_(this);
    }
}
