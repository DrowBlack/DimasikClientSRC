package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.TypeReferences;

public class KeyOptionsTranslation
extends DataFix {
    public KeyOptionsTranslation(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(TypeReferences.OPTIONS), p_209667_0_ -> p_209667_0_.update(DSL.remainderFinder(), p_209668_0_ -> p_209668_0_.getMapValues().map(p_209669_1_ -> p_209668_0_.createMap(p_209669_1_.entrySet().stream().map(p_209666_1_ -> {
            String s;
            if (((Dynamic)p_209666_1_.getKey()).asString("").startsWith("key_") && !(s = ((Dynamic)p_209666_1_.getValue()).asString("")).startsWith("key.mouse") && !s.startsWith("scancode.")) {
                return Pair.of((Dynamic)p_209666_1_.getKey(), p_209668_0_.createString("key.keyboard." + s.substring("key.".length())));
            }
            return Pair.of((Dynamic)p_209666_1_.getKey(), (Dynamic)p_209666_1_.getValue());
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))).result().orElse((Dynamic)p_209668_0_)));
    }
}
