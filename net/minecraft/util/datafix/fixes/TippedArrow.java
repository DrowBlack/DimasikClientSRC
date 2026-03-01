package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.TypedEntityRenameHelper;

public class TippedArrow
extends TypedEntityRenameHelper {
    public TippedArrow(Schema outputSchema, boolean changesType) {
        super("EntityTippedArrowFix", outputSchema, changesType);
    }

    @Override
    protected String rename(String name) {
        return Objects.equals(name, "TippedArrow") ? "Arrow" : name;
    }
}
