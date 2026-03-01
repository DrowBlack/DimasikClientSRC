package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.fixes.AbstractUUIDFix;
import net.minecraft.util.datafix.fixes.EntityUUID;

public class PlayerUUID
extends AbstractUUIDFix {
    public PlayerUUID(Schema p_i231461_1_) {
        super(p_i231461_1_, TypeReferences.PLAYER);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.reference), p_233353_0_ -> {
            OpticFinder<?> opticfinder = p_233353_0_.getType().findField("RootVehicle");
            return p_233353_0_.updateTyped(opticfinder, opticfinder.type(), p_233354_0_ -> p_233354_0_.update(DSL.remainderFinder(), p_233356_0_ -> PlayerUUID.func_233064_c_(p_233356_0_, "Attach", "Attach").orElse((Dynamic<?>)p_233356_0_))).update(DSL.remainderFinder(), p_233355_0_ -> EntityUUID.func_233214_c_(EntityUUID.func_233212_b_(p_233355_0_)));
        });
    }
}
