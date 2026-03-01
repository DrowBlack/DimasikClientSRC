package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.versions.V0100;
import net.minecraft.util.datafix.versions.V0705;

public class V1460
extends NamespacedSchema {
    public V1460(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> V0100.equipment(schema));
    }

    protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
        HashMap<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
        p_registerEntities_1_.registerSimple(map, "minecraft:area_effect_cloud");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:armor_stand");
        p_registerEntities_1_.register(map, "minecraft:arrow", (String p_206552_1_) -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:bat");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:blaze");
        p_registerEntities_1_.registerSimple(map, "minecraft:boat");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:cave_spider");
        p_registerEntities_1_.register(map, "minecraft:chest_minecart", (String p_206546_1_) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:chicken");
        p_registerEntities_1_.register(map, "minecraft:commandblock_minecart", (String p_206529_1_) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:cow");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:creeper");
        p_registerEntities_1_.register(map, "minecraft:donkey", (String p_206533_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:dragon_fireball");
        p_registerEntities_1_.registerSimple(map, "minecraft:egg");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:elder_guardian");
        p_registerEntities_1_.registerSimple(map, "minecraft:ender_crystal");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:ender_dragon");
        p_registerEntities_1_.register(map, "minecraft:enderman", (String p_206523_1_) -> DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:endermite");
        p_registerEntities_1_.registerSimple(map, "minecraft:ender_pearl");
        p_registerEntities_1_.registerSimple(map, "minecraft:evocation_fangs");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:evocation_illager");
        p_registerEntities_1_.registerSimple(map, "minecraft:eye_of_ender_signal");
        p_registerEntities_1_.register(map, "minecraft:falling_block", (String p_206524_1_) -> DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:fireball");
        p_registerEntities_1_.register(map, "minecraft:fireworks_rocket", (String p_206554_1_) -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:furnace_minecart", (String p_206515_1_) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:ghast");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:giant");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:guardian");
        p_registerEntities_1_.register(map, "minecraft:hopper_minecart", (String p_206541_1_) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        p_registerEntities_1_.register(map, "minecraft:horse", (String p_206545_1_) -> DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:husk");
        p_registerEntities_1_.registerSimple(map, "minecraft:illusion_illager");
        p_registerEntities_1_.register(map, "minecraft:item", (String p_206520_1_) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:item_frame", (String p_206535_1_) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:leash_knot");
        p_registerEntities_1_.register(map, "minecraft:llama", (String p_209327_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "DecorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:llama_spit");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:magma_cube");
        p_registerEntities_1_.register(map, "minecraft:minecart", (String p_206555_1_) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:mooshroom");
        p_registerEntities_1_.register(map, "minecraft:mule", (String p_206526_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:ocelot");
        p_registerEntities_1_.registerSimple(map, "minecraft:painting");
        p_registerEntities_1_.registerSimple(map, "minecraft:parrot");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:pig");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:polar_bear");
        p_registerEntities_1_.register(map, "minecraft:potion", (String p_206542_1_) -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:rabbit");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:sheep");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:shulker");
        p_registerEntities_1_.registerSimple(map, "minecraft:shulker_bullet");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:silverfish");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:skeleton");
        p_registerEntities_1_.register(map, "minecraft:skeleton_horse", (String p_206516_1_) -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:slime");
        p_registerEntities_1_.registerSimple(map, "minecraft:small_fireball");
        p_registerEntities_1_.registerSimple(map, "minecraft:snowball");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:snowman");
        p_registerEntities_1_.register(map, "minecraft:spawner_minecart", (String p_206527_1_) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:spectral_arrow", (String p_206522_1_) -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:spider");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:squid");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:stray");
        p_registerEntities_1_.registerSimple(map, "minecraft:tnt");
        p_registerEntities_1_.register(map, "minecraft:tnt_minecart", (String p_206551_1_) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:vex");
        p_registerEntities_1_.register(map, "minecraft:villager", (String p_206534_1_) -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0100.equipment(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:villager_golem");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:vindication_illager");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:witch");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:wither");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:wither_skeleton");
        p_registerEntities_1_.registerSimple(map, "minecraft:wither_skull");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:wolf");
        p_registerEntities_1_.registerSimple(map, "minecraft:xp_bottle");
        p_registerEntities_1_.registerSimple(map, "minecraft:xp_orb");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:zombie");
        p_registerEntities_1_.register(map, "minecraft:zombie_horse", (String p_206521_1_) -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:zombie_pigman");
        V1460.registerEntity(p_registerEntities_1_, map, "minecraft:zombie_villager");
        return map;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
        HashMap<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:furnace");
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:chest");
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:trapped_chest");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:ender_chest");
        p_registerBlockEntities_1_.register(map, "minecraft:jukebox", (String p_206549_1_) -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_)));
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:dispenser");
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:dropper");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:sign");
        p_registerBlockEntities_1_.register(map, "minecraft:mob_spawner", (String p_206530_1_) -> TypeReferences.UNTAGGED_SPAWNER.in(p_registerBlockEntities_1_));
        p_registerBlockEntities_1_.register(map, "minecraft:piston", (String p_206518_1_) -> DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(p_registerBlockEntities_1_)));
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:brewing_stand");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:enchanting_table");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:end_portal");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:beacon");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:skull");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:daylight_detector");
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:hopper");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:comparator");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:banner");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:structure_block");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:end_gateway");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:command_block");
        V1460.registerInventory(p_registerBlockEntities_1_, map, "minecraft:shulker_box");
        p_registerBlockEntities_1_.registerSimple(map, "minecraft:bed");
        return map;
    }

    @Override
    public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
        p_registerTypes_1_.registerType(false, TypeReferences.LEVEL, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.RECIPE, () -> DSL.constType(V1460.func_233457_a_()));
        p_registerTypes_1_.registerType(false, TypeReferences.PLAYER, () -> DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "ShoulderEntityRight", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(p_registerTypes_1_)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(p_registerTypes_1_))))));
        p_registerTypes_1_.registerType(false, TypeReferences.CHUNK, () -> DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_)))))));
        p_registerTypes_1_.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", V1460.func_233457_a_(), p_registerTypes_3_));
        p_registerTypes_1_.registerType(true, TypeReferences.ENTITY_TYPE, () -> DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), TypeReferences.ENTITY.in(p_registerTypes_1_)));
        p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", V1460.func_233457_a_(), p_registerTypes_2_));
        p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(p_registerTypes_1_), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), V0705.field_206597_b, Hook.HookFunction.IDENTITY));
        p_registerTypes_1_.registerType(false, TypeReferences.HOTBAR, () -> DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_))));
        p_registerTypes_1_.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_))));
        p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_NAME, () -> DSL.constType(V1460.func_233457_a_()));
        p_registerTypes_1_.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(V1460.func_233457_a_()));
        p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
        Supplier<TypeTemplate> supplier = () -> DSL.compoundList(TypeReferences.ITEM_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType()));
        p_registerTypes_1_.registerType(false, TypeReferences.STATS, () -> DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate)supplier.get(), "minecraft:used", (TypeTemplate)supplier.get(), "minecraft:broken", (TypeTemplate)supplier.get(), "minecraft:picked_up", (TypeTemplate)supplier.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate)supplier.get(), "minecraft:killed", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(V1460.func_233457_a_()), DSL.constType(DSL.intType()))))));
        p_registerTypes_1_.registerType(false, TypeReferences.SAVED_DATA, () -> DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(p_registerTypes_1_)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(p_registerTypes_1_)), "Teams", DSL.list(TypeReferences.TEAM.in(p_registerTypes_1_)))));
        p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CB", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CC", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_), "CD", TypeReferences.BLOCK_STATE.in(p_registerTypes_1_)))));
        p_registerTypes_1_.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.TEAM, DSL::remainder);
        p_registerTypes_1_.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "SpawnData", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)));
        p_registerTypes_1_.registerType(false, TypeReferences.ADVANCEMENTS, () -> DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.BIOME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(p_registerTypes_1_), DSL.constType(DSL.string())))));
        p_registerTypes_1_.registerType(false, TypeReferences.BIOME, () -> DSL.constType(V1460.func_233457_a_()));
        p_registerTypes_1_.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(V1460.func_233457_a_()));
        p_registerTypes_1_.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
        p_registerTypes_1_.registerType(true, TypeReferences.WORLD_GEN_SETTINGS, DSL::remainder);
    }
}
