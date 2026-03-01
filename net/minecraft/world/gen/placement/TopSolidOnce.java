package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.SimpleHeightmapBasedPlacement;

public class TopSolidOnce
extends SimpleHeightmapBasedPlacement<NoPlacementConfig> {
    public TopSolidOnce(Codec<NoPlacementConfig> p_i232096_1_) {
        super(p_i232096_1_);
    }

    @Override
    protected Heightmap.Type func_241858_a(NoPlacementConfig p_241858_1_) {
        return Heightmap.Type.OCEAN_FLOOR_WG;
    }
}
