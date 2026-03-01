package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class MinecartEntityTypes
extends DataFix {
    private static final List<String> MINECART_TYPE_LIST = Lists.newArrayList("MinecartRideable", "MinecartChest", "MinecartFurnace");

    public MinecartEntityTypes(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype1 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", taggedchoicetype, taggedchoicetype1, p_209746_2_ -> p_206328_3_ -> {
            if (!Objects.equals(p_206328_3_.getFirst(), "Minecart")) {
                return p_206328_3_;
            }
            Typed<Pair<String, ?>> typed = taggedchoicetype.point((DynamicOps<?>)p_209746_2_, "Minecart", p_206328_3_.getSecond()).orElseThrow(IllegalStateException::new);
            Dynamic<?> dynamic = typed.getOrCreate(DSL.remainderFinder());
            int i = dynamic.get("Type").asInt(0);
            String s = i > 0 && i < MINECART_TYPE_LIST.size() ? MINECART_TYPE_LIST.get(i) : "MinecartRideable";
            return Pair.of(s, typed.write().map(p_233177_2_ -> taggedchoicetype1.types().get(s).read(p_233177_2_)).result().orElseThrow(() -> new IllegalStateException("Could not read the new minecart.")));
        });
    }
}
