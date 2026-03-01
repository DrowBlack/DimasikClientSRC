package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice;

public class AddNewChoices
extends DataFix {
    private final String name;
    private final DSL.TypeReference type;

    public AddNewChoices(Schema outputSchema, String name, DSL.TypeReference type) {
        super(outputSchema, true);
        this.name = name;
        this.type = type;
    }

    @Override
    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype = this.getInputSchema().findChoiceType(this.type);
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype1 = this.getOutputSchema().findChoiceType(this.type);
        return this.cap(this.name, taggedchoicetype, taggedchoicetype1);
    }

    protected final <K> TypeRewriteRule cap(String p_206290_1_, TaggedChoice.TaggedChoiceType<K> p_206290_2_, TaggedChoice.TaggedChoiceType<?> p_206290_3_) {
        if (p_206290_2_.getKeyType() != p_206290_3_.getKeyType()) {
            throw new IllegalStateException("Could not inject: key type is not the same");
        }
        return this.fixTypeEverywhere(p_206290_1_, p_206290_2_, p_206290_3_, p_209687_2_ -> p_206291_2_ -> {
            if (!p_206290_3_.hasType(p_206291_2_.getFirst())) {
                throw new IllegalArgumentException(String.format("Unknown type %s in %s ", p_206291_2_.getFirst(), this.type));
            }
            return p_206291_2_;
        });
    }
}
