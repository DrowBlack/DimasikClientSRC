package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.versions.V0100;

public class V1470
extends NamespacedSchema {
    public V1470(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> V0100.equipment(schema));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:turtle");
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:cod_mob");
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:tropical_fish");
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:salmon_mob");
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:puffer_fish");
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:phantom");
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:dolphin");
        V1470.registerEntity(p_registerEntities_1_, map, "minecraft:drowned");
        p_registerEntities_1_.register(map, "minecraft:trident", (String p_206561_1_) -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        return map;
    }
}
