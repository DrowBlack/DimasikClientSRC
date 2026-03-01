package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

public class WallOrFloorItem
extends BlockItem {
    protected final Block wallBlock;

    public WallOrFloorItem(Block floorBlock, Block wallBlockIn, Item.Properties propertiesIn) {
        super(floorBlock, propertiesIn);
        this.wallBlock = wallBlockIn;
    }

    @Override
    @Nullable
    protected BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.wallBlock.getStateForPlacement(context);
        BlockState blockstate1 = null;
        World iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        for (Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate2;
            if (direction == Direction.UP) continue;
            BlockState blockState = blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(context) : blockstate;
            if (blockstate2 == null || !blockstate2.isValidPosition(iworldreader, blockpos)) continue;
            blockstate1 = blockstate2;
            break;
        }
        return blockstate1 != null && iworldreader.placedBlockCollides(blockstate1, blockpos, ISelectionContext.dummy()) ? blockstate1 : null;
    }

    @Override
    public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        super.addToBlockToItemMap(blockToItemMap, itemIn);
        blockToItemMap.put(this.wallBlock, itemIn);
    }
}
