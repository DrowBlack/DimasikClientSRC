package com.mojang.datafixers.types.families;

import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.types.Type;
import java.util.function.IntFunction;

public interface TypeFamily {
    public Type<?> apply(int var1);

    public static <A, B> FamilyOptic<A, B> familyOptic(IntFunction<OpticParts<A, B>> optics) {
        return optics::apply;
    }
}
