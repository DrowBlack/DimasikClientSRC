package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class PointOfInterestRebuild
extends DataFix {
    public PointOfInterestRebuild(Schema p_i225702_1_, boolean p_i225702_2_) {
        super(p_i225702_1_, p_i225702_2_);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<Pair<String, Dynamic<?>>> type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
        if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        }
        return this.fixTypeEverywhere("POI rebuild", type, p_226196_0_ -> p_226199_0_ -> p_226199_0_.mapSecond(PointOfInterestRebuild::func_226195_a_));
    }

    private static <T> Dynamic<T> func_226195_a_(Dynamic<T> p_226195_0_) {
        return p_226195_0_.update("Sections", p_226198_0_ -> p_226198_0_.updateMapValues(p_226197_0_ -> p_226197_0_.mapSecond(p_226200_0_ -> p_226200_0_.remove("Valid"))));
    }
}
