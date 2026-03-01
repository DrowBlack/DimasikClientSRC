package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.fixes.NamedEntityFix;

public class ShulkerRotation
extends NamedEntityFix {
    public ShulkerRotation(Schema p_i231451_1_) {
        super(p_i231451_1_, false, "EntityShulkerRotationFix", TypeReferences.ENTITY, "minecraft:shulker");
    }

    public Dynamic<?> func_233201_a_(Dynamic<?> p_233201_1_) {
        List<Double> list = p_233201_1_.get("Rotation").asList(p_233202_0_ -> p_233202_0_.asDouble(180.0));
        if (!list.isEmpty()) {
            list.set(0, list.get(0) - 180.0);
            return p_233201_1_.set("Rotation", p_233201_1_.createList(list.stream().map(p_233201_1_::createDouble)));
        }
        return p_233201_1_;
    }

    @Override
    protected Typed<?> fix(Typed<?> p_207419_1_) {
        return p_207419_1_.update(DSL.remainderFinder(), this::func_233201_a_);
    }
}
