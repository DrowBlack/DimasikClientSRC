package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.versions.V0099;
import net.minecraft.util.datafix.versions.V0100;
import net.minecraft.util.datafix.versions.V0704;

public class V0705
extends NamespacedSchema {
    protected static final Hook.HookFunction field_206597_b = new Hook.HookFunction(){

        @Override
        public <T> T apply(DynamicOps<T> p_apply_1_, T p_apply_2_) {
            return V0099.func_209869_a(new Dynamic<T>(p_apply_1_, p_apply_2_), V0704.field_206647_b, "minecraft:armor_stand");
        }
    };

    public V0705(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> V0100.equipment(schema));
    }

    protected static void registerThrowableProjectile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
        HashMap<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
        p_registerEntities_1_.registerSimple(map, "minecraft:area_effect_cloud");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:armor_stand");
        p_registerEntities_1_.register(map, "minecraft:arrow", (String p_206582_1_) -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:bat");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:blaze");
        p_registerEntities_1_.registerSimple(map, "minecraft:boat");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:cave_spider");
        p_registerEntities_1_.register(map, "minecraft:chest_minecart", (String p_206574_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:chicken");
        p_registerEntities_1_.register(map, "minecraft:commandblock_minecart", (String p_206575_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:cow");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:creeper");
        p_registerEntities_1_.register(map, "minecraft:donkey", (String p_206594_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:dragon_fireball");
        V0705.registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:egg");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:elder_guardian");
        p_registerEntities_1_.registerSimple(map, "minecraft:ender_crystal");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:ender_dragon");
        p_registerEntities_1_.register(map, "minecraft:enderman", (String p_206567_1_) -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:endermite");
        V0705.registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:ender_pearl");
        p_registerEntities_1_.registerSimple(map, "minecraft:eye_of_ender_signal");
        p_registerEntities_1_.register(map, "minecraft:falling_block", (String p_206586_1_) -> DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_)));
        V0705.registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:fireball");
        p_registerEntities_1_.register(map, "minecraft:fireworks_rocket", (String p_206588_1_) -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:furnace_minecart", (String p_206570_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:ghast");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:giant");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:guardian");
        p_registerEntities_1_.register(map, "minecraft:hopper_minecart", (String p_206584_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        p_registerEntities_1_.register(map, "minecraft:horse", (String p_206595_1_) -> DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:husk");
        p_registerEntities_1_.register(map, "minecraft:item", (String p_206578_1_) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:item_frame", (String p_206587_1_) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:leash_knot");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:magma_cube");
        p_registerEntities_1_.register(map, "minecraft:minecart", (String p_206568_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:mooshroom");
        p_registerEntities_1_.register(map, "minecraft:mule", (String p_206579_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:ocelot");
        p_registerEntities_1_.registerSimple(map, "minecraft:painting");
        p_registerEntities_1_.registerSimple(map, "minecraft:parrot");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:pig");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:polar_bear");
        p_registerEntities_1_.register(map, "minecraft:potion", (String p_206573_1_) -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:rabbit");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:sheep");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:shulker");
        p_registerEntities_1_.registerSimple(map, "minecraft:shulker_bullet");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:silverfish");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:skeleton");
        p_registerEntities_1_.register(map, "minecraft:skeleton_horse", (String p_206592_1_) -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:slime");
        V0705.registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:small_fireball");
        V0705.registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:snowball");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:snowman");
        p_registerEntities_1_.register(map, "minecraft:spawner_minecart", (String p_206583_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:spectral_arrow", (String p_206571_1_) -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:spider");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:squid");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:stray");
        p_registerEntities_1_.registerSimple(map, "minecraft:tnt");
        p_registerEntities_1_.register(map, "minecraft:tnt_minecart", (String p_206591_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "minecraft:villager", (String p_206580_1_) -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0100.equipment(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:villager_golem");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:witch");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:wither");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:wither_skeleton");
        V0705.registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:wither_skull");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:wolf");
        V0705.registerThrowableProjectile(p_registerEntities_1_, map, "minecraft:xp_bottle");
        p_registerEntities_1_.registerSimple(map, "minecraft:xp_orb");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:zombie");
        p_registerEntities_1_.register(map, "minecraft:zombie_horse", (String p_206569_1_) -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:zombie_pigman");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:zombie_villager");
        p_registerEntities_1_.registerSimple(map, "minecraft:evocation_fangs");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:evocation_illager");
        p_registerEntities_1_.registerSimple(map, "minecraft:illusion_illager");
        p_registerEntities_1_.register(map, "minecraft:llama", (String p_209329_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "DecorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "minecraft:llama_spit");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:vex");
        V0705.registerEntity(p_registerEntities_1_, map, "minecraft:vindication_illager");
        return map;
    }

    @Override
    public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
        super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
        p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", V0705.func_233457_a_(), p_registerTypes_2_));
        p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(p_registerTypes_1_), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), field_206597_b, Hook.HookFunction.IDENTITY));
    }
}
