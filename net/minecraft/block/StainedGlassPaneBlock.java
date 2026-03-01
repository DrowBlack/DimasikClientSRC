package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.block.PaneBlock;
import net.minecraft.item.DyeColor;

public class StainedGlassPaneBlock
extends PaneBlock
implements IBeaconBeamColorProvider {
    private final DyeColor color;

    public StainedGlassPaneBlock(DyeColor colorIn, AbstractBlock.Properties properties) {
        super(properties);
        this.color = colorIn;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}
