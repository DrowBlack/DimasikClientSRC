package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.TypedEntityRenameHelper;

public class EntityRavagerRenameFix
extends TypedEntityRenameHelper {
    public static final Map<String, String> field_219829_a = ImmutableMap.builder().put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg").build();

    public EntityRavagerRenameFix(Schema p_i50427_1_, boolean p_i50427_2_) {
        super("EntityRavagerRenameFix", p_i50427_1_, p_i50427_2_);
    }

    @Override
    protected String rename(String name) {
        return Objects.equals("minecraft:illager_beast", name) ? "minecraft:ravager" : name;
    }
}
