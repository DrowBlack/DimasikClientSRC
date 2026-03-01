package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0100
extends Schema {
    public V0100(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static TypeTemplate equipment(Schema schema) {
        return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
    }

    protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> V0100.equipment(schema));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
        V0100.registerEntity(p_registerEntities_1_, map, "ArmorStand");
        V0100.registerEntity(p_registerEntities_1_, map, "Creeper");
        V0100.registerEntity(p_registerEntities_1_, map, "Skeleton");
        V0100.registerEntity(p_registerEntities_1_, map, "Spider");
        V0100.registerEntity(p_registerEntities_1_, map, "Giant");
        V0100.registerEntity(p_registerEntities_1_, map, "Zombie");
        V0100.registerEntity(p_registerEntities_1_, map, "Slime");
        V0100.registerEntity(p_registerEntities_1_, map, "Ghast");
        V0100.registerEntity(p_registerEntities_1_, map, "PigZombie");
        p_registerEntities_1_.register(map, "Enderman", (String p_206609_1_) -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V0100.registerEntity(p_registerEntities_1_, map, "CaveSpider");
        V0100.registerEntity(p_registerEntities_1_, map, "Silverfish");
        V0100.registerEntity(p_registerEntities_1_, map, "Blaze");
        V0100.registerEntity(p_registerEntities_1_, map, "LavaSlime");
        V0100.registerEntity(p_registerEntities_1_, map, "EnderDragon");
        V0100.registerEntity(p_registerEntities_1_, map, "WitherBoss");
        V0100.registerEntity(p_registerEntities_1_, map, "Bat");
        V0100.registerEntity(p_registerEntities_1_, map, "Witch");
        V0100.registerEntity(p_registerEntities_1_, map, "Endermite");
        V0100.registerEntity(p_registerEntities_1_, map, "Guardian");
        V0100.registerEntity(p_registerEntities_1_, map, "Pig");
        V0100.registerEntity(p_registerEntities_1_, map, "Sheep");
        V0100.registerEntity(p_registerEntities_1_, map, "Cow");
        V0100.registerEntity(p_registerEntities_1_, map, "Chicken");
        V0100.registerEntity(p_registerEntities_1_, map, "Squid");
        V0100.registerEntity(p_registerEntities_1_, map, "Wolf");
        V0100.registerEntity(p_registerEntities_1_, map, "MushroomCow");
        V0100.registerEntity(p_registerEntities_1_, map, "SnowMan");
        V0100.registerEntity(p_registerEntities_1_, map, "Ozelot");
        V0100.registerEntity(p_registerEntities_1_, map, "VillagerGolem");
        p_registerEntities_1_.register(map, "EntityHorse", (String p_206612_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_)));
        V0100.registerEntity(p_registerEntities_1_, map, "Rabbit");
        p_registerEntities_1_.register(map, "Villager", (String p_206608_1_) -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0100.equipment(p_registerEntities_1_)));
        V0100.registerEntity(p_registerEntities_1_, map, "Shulker");
        p_registerEntities_1_.registerSimple(map, "AreaEffectCloud");
        p_registerEntities_1_.registerSimple(map, "ShulkerBullet");
        return map;
    }

    @Override
    public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
        super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
        p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_))));
        p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
    }
}
