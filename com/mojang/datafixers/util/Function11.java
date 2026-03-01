package com.mojang.datafixers.util;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> {
    public R apply(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, T6 var6, T7 var7, T8 var8, T9 var9, T10 var10, T11 var11);

    default public Function<T1, Function10<T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>> curry() {
        return t1 -> (t2, t3, t4, t5, t6, t7, t8, t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public BiFunction<T1, T2, Function9<T3, T4, T5, T6, T7, T8, T9, T10, T11, R>> curry2() {
        return (t1, t2) -> (t3, t4, t5, t6, t7, t8, t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function3<T1, T2, T3, Function8<T4, T5, T6, T7, T8, T9, T10, T11, R>> curry3() {
        return (t1, t2, t3) -> (t4, t5, t6, t7, t8, t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function4<T1, T2, T3, T4, Function7<T5, T6, T7, T8, T9, T10, T11, R>> curry4() {
        return (t1, t2, t3, t4) -> (t5, t6, t7, t8, t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function5<T1, T2, T3, T4, T5, Function6<T6, T7, T8, T9, T10, T11, R>> curry5() {
        return (t1, t2, t3, t4, t5) -> (t6, t7, t8, t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function6<T1, T2, T3, T4, T5, T6, Function5<T7, T8, T9, T10, T11, R>> curry6() {
        return (t1, t2, t3, t4, t5, t6) -> (t7, t8, t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function7<T1, T2, T3, T4, T5, T6, T7, Function4<T8, T9, T10, T11, R>> curry7() {
        return (t1, t2, t3, t4, t5, t6, t7) -> (t8, t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function8<T1, T2, T3, T4, T5, T6, T7, T8, Function3<T9, T10, T11, R>> curry8() {
        return (t1, t2, t3, t4, t5, t6, t7, t8) -> (t9, t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, BiFunction<T10, T11, R>> curry9() {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> (t10, t11) -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    default public Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Function<T11, R>> curry10() {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10) -> t11 -> this.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }
}
