package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public abstract class NamedEntityFix
extends DataFix {
    private final String name;
    private final String entityName;
    private final DSL.TypeReference type;

    public NamedEntityFix(Schema outputSchema, boolean changesType, String name, DSL.TypeReference type, String entityName) {
        super(outputSchema, changesType);
        this.name = name;
        this.type = type;
        this.entityName = entityName;
    }

    @Override
    public TypeRewriteRule makeRule() {
        OpticFinder<?> opticfinder = DSL.namedChoice(this.entityName, this.getInputSchema().getChoiceType(this.type, this.entityName));
        return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (Typed<?> p_206371_2_) -> p_206371_2_.updateTyped(opticfinder, this.getOutputSchema().getChoiceType(this.type, this.entityName), this::fix));
    }

    protected abstract Typed<?> fix(Typed<?> var1);
}
