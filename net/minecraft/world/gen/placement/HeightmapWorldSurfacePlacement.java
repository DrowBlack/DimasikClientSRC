package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.SimpleHeightmapBasedPlacement;

public class HeightmapWorldSurfacePlacement
extends SimpleHeightmapBasedPlacement<NoPlacementConfig> {
    public HeightmapWorldSurfacePlacement(Codec<NoPlacementConfig> p_i242025_1_) {
        super(p_i242025_1_);
    }

    @Override
    protected Heightmap.Type func_241858_a(NoPlacementConfig p_241858_1_) {
        return Heightmap.Type.WORLD_SURFACE_WG;
    }
}
