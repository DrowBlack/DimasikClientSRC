package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.datafix.fixes.BlockStateFlatteningMap;

public class BlockStateFlattenStructures
extends DataFix {
    public BlockStateFlattenStructures(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("BlockStateStructureTemplateFix", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), p_207440_0_ -> p_207440_0_.update(DSL.remainderFinder(), BlockStateFlatteningMap::updateNBT));
    }
}
