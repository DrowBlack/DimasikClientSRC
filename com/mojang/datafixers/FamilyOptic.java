package com.mojang.datafixers;

import com.mojang.datafixers.OpticParts;

public interface FamilyOptic<A, B> {
    public OpticParts<A, B> apply(int var1);
}
