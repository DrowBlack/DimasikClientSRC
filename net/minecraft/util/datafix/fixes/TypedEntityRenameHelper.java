package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public abstract class TypedEntityRenameHelper
extends DataFix {
    private final String name;

    public TypedEntityRenameHelper(String name, Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        TaggedChoice.TaggedChoiceType<?> taggedchoicetype1 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        Type<Pair<String, String>> type = DSL.named(TypeReferences.ENTITY_NAME.typeName(), NamespacedSchema.func_233457_a_());
        if (!Objects.equals(this.getOutputSchema().getType(TypeReferences.ENTITY_NAME), type)) {
            throw new IllegalStateException("Entity name type is not what was expected.");
        }
        return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, taggedchoicetype, taggedchoicetype1, p_233400_3_ -> p_211307_3_ -> p_211307_3_.mapFirst(p_211309_3_ -> {
            String s = this.rename((String)p_211309_3_);
            Type<?> type1 = taggedchoicetype.types().get(p_211309_3_);
            Type<?> type2 = taggedchoicetype1.types().get(s);
            if (!type2.equals(type1, true, true)) {
                throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, type1));
            }
            return s;
        })), this.fixTypeEverywhere(this.name + " for entity name", type, p_211308_1_ -> p_211310_1_ -> p_211310_1_.mapSecond(this::rename)));
    }

    protected abstract String rename(String var1);
}
