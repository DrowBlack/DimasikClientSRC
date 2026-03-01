package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.versions.V0100;

public class V1451_3
extends NamespacedSchema {
    public V1451_3(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        p_registerEntities_1_.registerSimple(map, "minecraft:egg");
        p_registerEntities_1_.registerSimple(map, "minecraft:ender_pearl");
        p_registerEntities_1_.registerSimple(map, "minecraft:fireball");
        p_registerEntities_1_.register(map, "minecraft:potion", (String p_206498_1_) -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:small_fireball");
        p_registerEntities_1_.registerSimple(map, "minecraft:snowball");
        p_registerEntities_1_.registerSimple(map, "minecraft:wither_skull");
        p_registerEntities_1_.registerSimple(map, "minecraft:xp_bottle");
        p_registerEntities_1_.register(map, "minecraft:arrow", () -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:enderman", () -> DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:falling_block", () -> DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:spectral_arrow", () -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:chest_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        p_registerEntities_1_.register(map, "minecraft:commandblock_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:furnace_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:hopper_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        p_registerEntities_1_.register(map, "minecraft:minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:spawner_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:tnt_minecart", () -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        return map;
    }
}
