package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class V0099
extends Schema {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, String> field_206693_d = DataFixUtils.make(Maps.newHashMap(), p_209320_0_ -> {
        p_209320_0_.put("minecraft:furnace", "Furnace");
        p_209320_0_.put("minecraft:lit_furnace", "Furnace");
        p_209320_0_.put("minecraft:chest", "Chest");
        p_209320_0_.put("minecraft:trapped_chest", "Chest");
        p_209320_0_.put("minecraft:ender_chest", "EnderChest");
        p_209320_0_.put("minecraft:jukebox", "RecordPlayer");
        p_209320_0_.put("minecraft:dispenser", "Trap");
        p_209320_0_.put("minecraft:dropper", "Dropper");
        p_209320_0_.put("minecraft:sign", "Sign");
        p_209320_0_.put("minecraft:mob_spawner", "MobSpawner");
        p_209320_0_.put("minecraft:noteblock", "Music");
        p_209320_0_.put("minecraft:brewing_stand", "Cauldron");
        p_209320_0_.put("minecraft:enhanting_table", "EnchantTable");
        p_209320_0_.put("minecraft:command_block", "CommandBlock");
        p_209320_0_.put("minecraft:beacon", "Beacon");
        p_209320_0_.put("minecraft:skull", "Skull");
        p_209320_0_.put("minecraft:daylight_detector", "DLDetector");
        p_209320_0_.put("minecraft:hopper", "Hopper");
        p_209320_0_.put("minecraft:banner", "Banner");
        p_209320_0_.put("minecraft:flower_pot", "FlowerPot");
        p_209320_0_.put("minecraft:repeating_command_block", "CommandBlock");
        p_209320_0_.put("minecraft:chain_command_block", "CommandBlock");
        p_209320_0_.put("minecraft:standing_sign", "Sign");
        p_209320_0_.put("minecraft:wall_sign", "Sign");
        p_209320_0_.put("minecraft:piston_head", "Piston");
        p_209320_0_.put("minecraft:daylight_detector_inverted", "DLDetector");
        p_209320_0_.put("minecraft:unpowered_comparator", "Comparator");
        p_209320_0_.put("minecraft:powered_comparator", "Comparator");
        p_209320_0_.put("minecraft:wall_banner", "Banner");
        p_209320_0_.put("minecraft:standing_banner", "Banner");
        p_209320_0_.put("minecraft:structure_block", "Structure");
        p_209320_0_.put("minecraft:end_portal", "Airportal");
        p_209320_0_.put("minecraft:end_gateway", "EndGateway");
        p_209320_0_.put("minecraft:shield", "Banner");
    });
    protected static final Hook.HookFunction field_206691_b = new Hook.HookFunction(){

        @Override
        public <T> T apply(DynamicOps<T> p_apply_1_, T p_apply_2_) {
            return V0099.func_209869_a(new Dynamic<T>(p_apply_1_, p_apply_2_), field_206693_d, "ArmorStand");
        }
    };

    public V0099(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static TypeTemplate equipment(Schema schema) {
        return DSL.optionalFields("Equipment", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
    }

    protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> V0099.equipment(schema));
    }

    protected static void registerThrowableProjectile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema)));
    }

    protected static void registerMinecart(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)));
    }

    protected static void registerInventory(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
        HashMap<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
        p_registerEntities_1_.register(map, "Item", (String p_206678_1_) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "XPOrb");
        V0099.registerThrowableProjectile(p_registerEntities_1_, map, "ThrownEgg");
        p_registerEntities_1_.registerSimple(map, "LeashKnot");
        p_registerEntities_1_.registerSimple(map, "Painting");
        p_registerEntities_1_.register(map, "Arrow", (String p_206682_1_) -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "TippedArrow", (String p_206655_1_) -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "SpectralArrow", (String p_206671_1_) -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_)));
        V0099.registerThrowableProjectile(p_registerEntities_1_, map, "Snowball");
        V0099.registerThrowableProjectile(p_registerEntities_1_, map, "Fireball");
        V0099.registerThrowableProjectile(p_registerEntities_1_, map, "SmallFireball");
        V0099.registerThrowableProjectile(p_registerEntities_1_, map, "ThrownEnderpearl");
        p_registerEntities_1_.registerSimple(map, "EyeOfEnderSignal");
        p_registerEntities_1_.register(map, "ThrownPotion", (String p_206688_1_) -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        V0099.registerThrowableProjectile(p_registerEntities_1_, map, "ThrownExpBottle");
        p_registerEntities_1_.register(map, "ItemFrame", (String p_206661_1_) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        V0099.registerThrowableProjectile(p_registerEntities_1_, map, "WitherSkull");
        p_registerEntities_1_.registerSimple(map, "PrimedTnt");
        p_registerEntities_1_.register(map, "FallingSand", (String p_206679_1_) -> DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "FireworksRocketEntity", (String p_206651_1_) -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "Boat");
        p_registerEntities_1_.register(map, "Minecart", () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        V0099.registerMinecart(p_registerEntities_1_, map, "MinecartRideable");
        p_registerEntities_1_.register(map, "MinecartChest", (String p_206663_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        V0099.registerMinecart(p_registerEntities_1_, map, "MinecartFurnace");
        V0099.registerMinecart(p_registerEntities_1_, map, "MinecartTNT");
        p_registerEntities_1_.register(map, "MinecartSpawner", () -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_)));
        p_registerEntities_1_.register(map, "MinecartHopper", (String p_210752_1_) -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_))));
        V0099.registerMinecart(p_registerEntities_1_, map, "MinecartCommandBlock");
        V0099.registerEntity(p_registerEntities_1_, map, "ArmorStand");
        V0099.registerEntity(p_registerEntities_1_, map, "Creeper");
        V0099.registerEntity(p_registerEntities_1_, map, "Skeleton");
        V0099.registerEntity(p_registerEntities_1_, map, "Spider");
        V0099.registerEntity(p_registerEntities_1_, map, "Giant");
        V0099.registerEntity(p_registerEntities_1_, map, "Zombie");
        V0099.registerEntity(p_registerEntities_1_, map, "Slime");
        V0099.registerEntity(p_registerEntities_1_, map, "Ghast");
        V0099.registerEntity(p_registerEntities_1_, map, "PigZombie");
        p_registerEntities_1_.register(map, "Enderman", (String p_206686_1_) -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), V0099.equipment(p_registerEntities_1_)));
        V0099.registerEntity(p_registerEntities_1_, map, "CaveSpider");
        V0099.registerEntity(p_registerEntities_1_, map, "Silverfish");
        V0099.registerEntity(p_registerEntities_1_, map, "Blaze");
        V0099.registerEntity(p_registerEntities_1_, map, "LavaSlime");
        V0099.registerEntity(p_registerEntities_1_, map, "EnderDragon");
        V0099.registerEntity(p_registerEntities_1_, map, "WitherBoss");
        V0099.registerEntity(p_registerEntities_1_, map, "Bat");
        V0099.registerEntity(p_registerEntities_1_, map, "Witch");
        V0099.registerEntity(p_registerEntities_1_, map, "Endermite");
        V0099.registerEntity(p_registerEntities_1_, map, "Guardian");
        V0099.registerEntity(p_registerEntities_1_, map, "Pig");
        V0099.registerEntity(p_registerEntities_1_, map, "Sheep");
        V0099.registerEntity(p_registerEntities_1_, map, "Cow");
        V0099.registerEntity(p_registerEntities_1_, map, "Chicken");
        V0099.registerEntity(p_registerEntities_1_, map, "Squid");
        V0099.registerEntity(p_registerEntities_1_, map, "Wolf");
        V0099.registerEntity(p_registerEntities_1_, map, "MushroomCow");
        V0099.registerEntity(p_registerEntities_1_, map, "SnowMan");
        V0099.registerEntity(p_registerEntities_1_, map, "Ozelot");
        V0099.registerEntity(p_registerEntities_1_, map, "VillagerGolem");
        p_registerEntities_1_.register(map, "EntityHorse", (String p_206670_1_) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0099.equipment(p_registerEntities_1_)));
        V0099.registerEntity(p_registerEntities_1_, map, "Rabbit");
        p_registerEntities_1_.register(map, "Villager", (String p_206656_1_) -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0099.equipment(p_registerEntities_1_)));
        p_registerEntities_1_.registerSimple(map, "EnderCrystal");
        p_registerEntities_1_.registerSimple(map, "AreaEffectCloud");
        p_registerEntities_1_.registerSimple(map, "ShulkerBullet");
        V0099.registerEntity(p_registerEntities_1_, map, "Shulker");
        return map;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
        HashMap<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
        V0099.registerInventory(p_registerBlockEntities_1_, map, "Furnace");
        V0099.registerInventory(p_registerBlockEntities_1_, map, "Chest");
        p_registerBlockEntities_1_.registerSimple(map, "EnderChest");
        p_registerBlockEntities_1_.register(map, "RecordPlayer", (String p_206684_1_) -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_)));
        V0099.registerInventory(p_registerBlockEntities_1_, map, "Trap");
        V0099.registerInventory(p_registerBlockEntities_1_, map, "Dropper");
        p_registerBlockEntities_1_.registerSimple(map, "Sign");
        p_registerBlockEntities_1_.register(map, "MobSpawner", (String p_206667_1_) -> TypeReferences.UNTAGGED_SPAWNER.in(p_registerBlockEntities_1_));
        p_registerBlockEntities_1_.registerSimple(map, "Music");
        p_registerBlockEntities_1_.registerSimple(map, "Piston");
        V0099.registerInventory(p_registerBlockEntities_1_, map, "Cauldron");
        p_registerBlockEntities_1_.registerSimple(map, "EnchantTable");
        p_registerBlockEntities_1_.registerSimple(map, "Airportal");
        p_registerBlockEntities_1_.registerSimple(map, "Control");
        p_registerBlockEntities_1_.registerSimple(map, "Beacon");
        p_registerBlockEntities_1_.registerSimple(map, "Skull");
        p_registerBlockEntities_1_.registerSimple(map, "DLDetector");
        V0099.registerInventory(p_registerBlockEntities_1_, map, "Hopper");
        p_registerBlockEntities_1_.registerSimple(map, "Comparator");
        p_registerBlockEntities_1_.register(map, "FlowerPot", (String p_206653_1_) -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(p_registerBlockEntities_1_))));
        p_registerBlockEntities_1_.registerSimple(map, "Banner");
        p_registerBlockEntities_1_.registerSimple(map, "Structure");
        p_registerBlockEntities_1_.registerSimple(map, "EndGateway");
        return map;
    }

    @Override
    public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
        p_registerTypes_1_.registerType(false, TypeReferences.LEVEL, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.PLAYER, () -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_))));
        p_registerTypes_1_.registerType(false, TypeReferences.CHUNK, () -> DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_))))));
        p_registerTypes_1_.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), p_registerTypes_3_));
        p_registerTypes_1_.registerType(true, TypeReferences.ENTITY_TYPE, () -> DSL.optionalFields("Riding", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), TypeReferences.ENTITY.in(p_registerTypes_1_)));
        p_registerTypes_1_.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(NamespacedSchema.func_233457_a_()));
        p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), p_registerTypes_2_));
        p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(p_registerTypes_1_)), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), field_206691_b, Hook.HookFunction.IDENTITY));
        p_registerTypes_1_.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_NAME, () -> DSL.or(DSL.constType(DSL.intType()), DSL.constType(NamespacedSchema.func_233457_a_())));
        p_registerTypes_1_.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(NamespacedSchema.func_233457_a_()));
        p_registerTypes_1_.registerType(false, TypeReferences.STATS, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.SAVED_DATA, () -> DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(p_registerTypes_1_)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(p_registerTypes_1_)), "Teams", DSL.list(TypeReferences.TEAM.in(p_registerTypes_1_)))));
        p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.TEAM, DSL::remainder);
        p_registerTypes_1_.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
        p_registerTypes_1_.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
        p_registerTypes_1_.registerType(true, TypeReferences.WORLD_GEN_SETTINGS, DSL::remainder);
    }

    protected static <T> T func_209869_a(Dynamic<T> p_209869_0_, Map<String, String> p_209869_1_, String p_209869_2_) {
        return p_209869_0_.update("tag", p_209868_3_ -> p_209868_3_.update("BlockEntityTag", p_209870_2_ -> {
            String s = p_209869_0_.get("id").asString("");
            String s1 = (String)p_209869_1_.get(NamespacedSchema.ensureNamespaced(s));
            if (s1 == null) {
                LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", (Object)s);
                return p_209870_2_;
            }
            return p_209870_2_.set("id", p_209869_0_.createString(s1));
        }).update("EntityTag", p_209866_2_ -> {
            String s = p_209869_0_.get("id").asString("");
            return Objects.equals(NamespacedSchema.ensureNamespaced(s), "minecraft:armor_stand") ? p_209866_2_.set("id", p_209869_0_.createString(p_209869_2_)) : p_209866_2_;
        })).getValue();
    }
}
