package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.versions.V0100;

public class V2568
extends NamespacedSchema {
    public V2568(int p_i241902_1_, Schema p_i241902_2_) {
        super(p_i241902_1_, p_i241902_2_);
    }

    protected static void func_242270_a(Schema p_242270_0_, Map<String, Supplier<TypeTemplate>> p_242270_1_, String p_242270_2_) {
        p_242270_0_.register(p_242270_1_, p_242270_2_, () -> V0100.equipment(p_242270_0_));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        V2568.func_242270_a(p_registerEntities_1_, map, "minecraft:piglin_brute");
        return map;
    }
}
