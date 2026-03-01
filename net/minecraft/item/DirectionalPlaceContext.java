package net.minecraft.item;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class DirectionalPlaceContext
extends BlockItemUseContext {
    private final Direction lookDirection;

    public DirectionalPlaceContext(World worldIn, BlockPos pos, Direction lookDirectionIn, ItemStack stackIn, Direction against) {
        super(worldIn, null, Hand.MAIN_HAND, stackIn, new BlockRayTraceResult(Vector3d.copyCenteredHorizontally(pos), against, pos, false));
        this.lookDirection = lookDirectionIn;
    }

    @Override
    public BlockPos getPos() {
        return this.func_242401_i().getPos();
    }

    @Override
    public boolean canPlace() {
        return this.getWorld().getBlockState(this.func_242401_i().getPos()).isReplaceable(this);
    }

    @Override
    public boolean replacingClickedOnBlock() {
        return this.canPlace();
    }

    @Override
    public Direction getNearestLookingDirection() {
        return Direction.DOWN;
    }

    @Override
    public Direction[] getNearestLookingDirections() {
        switch (this.lookDirection) {
            default: {
                return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
            }
            case UP: {
                return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            }
            case NORTH: {
                return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
            }
            case SOUTH: {
                return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
            }
            case WEST: {
                return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
            }
            case EAST: 
        }
        return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
    }

    @Override
    public Direction getPlacementHorizontalFacing() {
        return this.lookDirection.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.lookDirection;
    }

    @Override
    public boolean hasSecondaryUseForPlayer() {
        return false;
    }

    @Override
    public float getPlacementYaw() {
        return this.lookDirection.getHorizontalIndex() * 90;
    }
}
