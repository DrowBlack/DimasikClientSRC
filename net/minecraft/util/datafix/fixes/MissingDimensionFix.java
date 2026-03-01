package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.fixes.WorldGenSettings;

public class MissingDimensionFix
extends DataFix {
    public MissingDimensionFix(Schema p_i241230_1_, boolean p_i241230_2_) {
        super(p_i241230_1_, p_i241230_2_);
    }

    private static <A> Type<Pair<A, Dynamic<?>>> func_241312_a_(String p_241312_0_, Type<A> p_241312_1_) {
        return DSL.and(DSL.field(p_241312_0_, p_241312_1_), DSL.remainderType());
    }

    private static <A> Type<Pair<Either<A, Unit>, Dynamic<?>>> func_241314_b_(String p_241314_0_, Type<A> p_241314_1_) {
        return DSL.and(DSL.optional(DSL.field(p_241314_0_, p_241314_1_)), DSL.remainderType());
    }

    private static <A1, A2> Type<Pair<Either<A1, Unit>, Pair<Either<A2, Unit>, Dynamic<?>>>> func_241313_a_(String p_241313_0_, Type<A1> p_241313_1_, String p_241313_2_, Type<A2> p_241313_3_) {
        return DSL.and(DSL.optional(DSL.field(p_241313_0_, p_241313_1_)), DSL.optional(DSL.field(p_241313_2_, p_241313_3_)), DSL.remainderType());
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        TaggedChoice.TaggedChoiceType<String> taggedchoicetype = new TaggedChoice.TaggedChoiceType<String>("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL.remainderType(), "minecraft:flat", MissingDimensionFix.func_241314_b_("settings", MissingDimensionFix.func_241313_a_("biome", schema.getType(TypeReferences.BIOME), "layers", DSL.list(MissingDimensionFix.func_241314_b_("block", schema.getType(TypeReferences.BLOCK_NAME))))), "minecraft:noise", MissingDimensionFix.func_241313_a_("biome_source", DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:fixed", MissingDimensionFix.func_241312_a_("biome", schema.getType(TypeReferences.BIOME)), "minecraft:multi_noise", DSL.list(MissingDimensionFix.func_241312_a_("biome", schema.getType(TypeReferences.BIOME))), "minecraft:checkerboard", MissingDimensionFix.func_241312_a_("biomes", DSL.list(schema.getType(TypeReferences.BIOME))), "minecraft:vanilla_layered", DSL.remainderType(), "minecraft:the_end", DSL.remainderType())), "settings", DSL.or(DSL.string(), MissingDimensionFix.func_241313_a_("default_block", schema.getType(TypeReferences.BLOCK_NAME), "default_fluid", schema.getType(TypeReferences.BLOCK_NAME))))));
        CompoundList.CompoundListType<String, Pair<String, Dynamic<?>>> compoundlisttype = DSL.compoundList(NamespacedSchema.func_233457_a_(), MissingDimensionFix.func_241312_a_("generator", taggedchoicetype));
        Type type = DSL.and(compoundlisttype, DSL.remainderType());
        Type<?> type1 = schema.getType(TypeReferences.WORLD_GEN_SETTINGS);
        FieldFinder fieldfinder = new FieldFinder("dimensions", type);
        if (!type1.findFieldType("dimensions").equals(type)) {
            throw new IllegalStateException();
        }
        OpticFinder opticfinder = compoundlisttype.finder();
        return this.fixTypeEverywhereTyped("MissingDimensionFix", type1, p_241308_4_ -> p_241308_4_.updateTyped(fieldfinder, p_241309_4_ -> p_241309_4_.updateTyped(opticfinder, p_241310_3_ -> {
            if (!(p_241310_3_.getValue() instanceof List)) {
                throw new IllegalStateException("List exptected");
            }
            if (((List)p_241310_3_.getValue()).isEmpty()) {
                Dynamic<?> dynamic = p_241308_4_.get(DSL.remainderFinder());
                Dynamic<?> dynamic1 = this.func_241311_a_(dynamic);
                return DataFixUtils.orElse(compoundlisttype.readTyped(dynamic1).result().map(Pair::getFirst), p_241310_3_);
            }
            return p_241310_3_;
        })));
    }

    private <T> Dynamic<T> func_241311_a_(Dynamic<T> p_241311_1_) {
        long i = p_241311_1_.get("seed").asLong(0L);
        return new Dynamic(p_241311_1_.getOps(), WorldGenSettings.func_241323_a_(p_241311_1_, i, WorldGenSettings.func_241322_a_(p_241311_1_, i), false));
    }
}
