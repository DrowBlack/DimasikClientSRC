package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.fixes.EntityRename;

public class HorseSplit
extends EntityRename {
    public HorseSplit(Schema outputSchema, boolean changesType) {
        super("EntityHorseSplitFix", outputSchema, changesType);
    }

    @Override
    protected Pair<String, Typed<?>> fix(String p_209149_1_, Typed<?> p_209149_2_) {
        Dynamic<?> dynamic = p_209149_2_.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", p_209149_1_)) {
            int i = dynamic.get("Type").asInt(0);
            String s = switch (i) {
                default -> "Horse";
                case 1 -> "Donkey";
                case 2 -> "Mule";
                case 3 -> "ZombieHorse";
                case 4 -> "SkeletonHorse";
            };
            dynamic.remove("Type");
            Type<?> type = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(s);
            return Pair.of(s, (Typed)((Pair)p_209149_2_.write().flatMap(type::readTyped).result().orElseThrow(() -> new IllegalStateException("Could not parse the new horse"))).getFirst());
        }
        return Pair.of(p_209149_1_, p_209149_2_);
    }
}
