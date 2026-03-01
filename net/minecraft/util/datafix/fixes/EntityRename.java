package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.datafix.TypeReferences;

public abstract class EntityRename
extends DataFix {
    protected final String name;

    public EntityRename(String name, Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype1 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        return this.fixTypeEverywhere(this.name, taggedchoicetype, taggedchoicetype1, p_209755_3_ -> p_209150_4_ -> {
            String s = (String)p_209150_4_.getFirst();
            Type<?> type = taggedchoicetype.types().get(s);
            Pair<String, Typed<?>> pair = this.fix(s, this.getEntity(p_209150_4_.getSecond(), (DynamicOps<?>)p_209755_3_, type));
            Type<?> type1 = taggedchoicetype1.types().get(pair.getFirst());
            if (!type1.equals(pair.getSecond().getType(), true, true)) {
                throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type1, pair.getSecond().getType()));
            }
            return Pair.of(pair.getFirst(), pair.getSecond().getValue());
        });
    }

    private <A> Typed<A> getEntity(Object p_209757_1_, DynamicOps<?> p_209757_2_, Type<A> p_209757_3_) {
        return new Typed<Object>(p_209757_3_, p_209757_2_, p_209757_1_);
    }

    protected abstract Pair<String, Typed<?>> fix(String var1, Typed<?> var2);
}
