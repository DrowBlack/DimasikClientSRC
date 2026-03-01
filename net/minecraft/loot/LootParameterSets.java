package net.minecraft.loot;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.ResourceLocation;

public class LootParameterSets {
    private static final BiMap<ResourceLocation, LootParameterSet> REGISTRY = HashBiMap.create();
    public static final LootParameterSet EMPTY = LootParameterSets.register("empty", p_216249_0_ -> {});
    public static final LootParameterSet CHEST = LootParameterSets.register("chest", p_216259_0_ -> p_216259_0_.required(LootParameters.field_237457_g_).optional(LootParameters.THIS_ENTITY));
    public static final LootParameterSet COMMAND = LootParameterSets.register("command", p_216250_0_ -> p_216250_0_.required(LootParameters.field_237457_g_).optional(LootParameters.THIS_ENTITY));
    public static final LootParameterSet SELECTOR = LootParameterSets.register("selector", p_216254_0_ -> p_216254_0_.required(LootParameters.field_237457_g_).required(LootParameters.THIS_ENTITY));
    public static final LootParameterSet FISHING = LootParameterSets.register("fishing", p_216258_0_ -> p_216258_0_.required(LootParameters.field_237457_g_).required(LootParameters.TOOL).optional(LootParameters.THIS_ENTITY));
    public static final LootParameterSet ENTITY = LootParameterSets.register("entity", p_216251_0_ -> p_216251_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.field_237457_g_).required(LootParameters.DAMAGE_SOURCE).optional(LootParameters.KILLER_ENTITY).optional(LootParameters.DIRECT_KILLER_ENTITY).optional(LootParameters.LAST_DAMAGE_PLAYER));
    public static final LootParameterSet GIFT = LootParameterSets.register("gift", p_216255_0_ -> p_216255_0_.required(LootParameters.field_237457_g_).required(LootParameters.THIS_ENTITY));
    public static final LootParameterSet field_237453_h_ = LootParameterSets.register("barter", p_216252_0_ -> p_216252_0_.required(LootParameters.THIS_ENTITY));
    public static final LootParameterSet ADVANCEMENT = LootParameterSets.register("advancement_reward", p_227560_0_ -> p_227560_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.field_237457_g_));
    public static final LootParameterSet field_237454_j_ = LootParameterSets.register("advancement_entity", p_227559_0_ -> p_227559_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.field_237457_g_));
    public static final LootParameterSet GENERIC = LootParameterSets.register("generic", p_237456_0_ -> p_237456_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.LAST_DAMAGE_PLAYER).required(LootParameters.DAMAGE_SOURCE).required(LootParameters.KILLER_ENTITY).required(LootParameters.DIRECT_KILLER_ENTITY).required(LootParameters.field_237457_g_).required(LootParameters.BLOCK_STATE).required(LootParameters.BLOCK_ENTITY).required(LootParameters.TOOL).required(LootParameters.EXPLOSION_RADIUS));
    public static final LootParameterSet BLOCK = LootParameterSets.register("block", p_237455_0_ -> p_237455_0_.required(LootParameters.BLOCK_STATE).required(LootParameters.field_237457_g_).required(LootParameters.TOOL).optional(LootParameters.THIS_ENTITY).optional(LootParameters.BLOCK_ENTITY).optional(LootParameters.EXPLOSION_RADIUS));

    private static LootParameterSet register(String registryName, Consumer<LootParameterSet.Builder> p_216253_1_) {
        LootParameterSet.Builder lootparameterset$builder = new LootParameterSet.Builder();
        p_216253_1_.accept(lootparameterset$builder);
        LootParameterSet lootparameterset = lootparameterset$builder.build();
        ResourceLocation resourcelocation = new ResourceLocation(registryName);
        LootParameterSet lootparameterset1 = REGISTRY.put(resourcelocation, lootparameterset);
        if (lootparameterset1 != null) {
            throw new IllegalStateException("Loot table parameter set " + String.valueOf(resourcelocation) + " is already registered");
        }
        return lootparameterset;
    }

    @Nullable
    public static LootParameterSet getValue(ResourceLocation registryName) {
        return (LootParameterSet)REGISTRY.get(registryName);
    }

    @Nullable
    public static ResourceLocation getKey(LootParameterSet p_216257_0_) {
        return (ResourceLocation)REGISTRY.inverse().get(p_216257_0_);
    }
}
