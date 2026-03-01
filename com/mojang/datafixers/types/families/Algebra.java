package com.mojang.datafixers.types.families;

import com.mojang.datafixers.RewriteResult;

public interface Algebra {
    public RewriteResult<?, ?> apply(int var1);

    public String toString(int var1);
}
