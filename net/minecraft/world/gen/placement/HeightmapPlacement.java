package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.SimpleHeightmapBasedPlacement;

public class HeightmapPlacement<DC extends IPlacementConfig>
extends SimpleHeightmapBasedPlacement<DC> {
    public HeightmapPlacement(Codec<DC> p_i242026_1_) {
        super(p_i242026_1_);
    }

    @Override
    protected Heightmap.Type func_241858_a(DC p_241858_1_) {
        return Heightmap.Type.MOTION_BLOCKING;
    }
}
