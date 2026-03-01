package dimasik.itemics.pathing.movement;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.pathing.movement.ActionCosts;
import dimasik.itemics.api.pathing.movement.MovementStatus;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.api.utils.RayTraceUtils;
import dimasik.itemics.api.utils.Rotation;
import dimasik.itemics.api.utils.RotationUtils;
import dimasik.itemics.api.utils.VecUtils;
import dimasik.itemics.api.utils.input.Input;
import dimasik.itemics.pathing.movement.CalculationContext;
import dimasik.itemics.pathing.movement.Movement;
import dimasik.itemics.pathing.movement.MovementState;
import dimasik.itemics.pathing.precompute.Ternary;
import dimasik.itemics.utils.BlockStateInterface;
import dimasik.itemics.utils.ToolSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.block.MovingPistonBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public interface MovementHelper
extends ActionCosts,
Helper {
    public static boolean avoidBreaking(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        if (!bsi.worldBorder.canPlaceAt(x, z)) {
            return true;
        }
        Block b = state.getBlock();
        return ((List)Itemics.settings().blocksToDisallowBreaking.value).contains(b) || b == Blocks.ICE || b instanceof SilverfishBlock || MovementHelper.avoidAdjacentBreaking(bsi, x, y + 1, z, true) || MovementHelper.avoidAdjacentBreaking(bsi, x + 1, y, z, false) || MovementHelper.avoidAdjacentBreaking(bsi, x - 1, y, z, false) || MovementHelper.avoidAdjacentBreaking(bsi, x, y, z + 1, false) || MovementHelper.avoidAdjacentBreaking(bsi, x, y, z - 1, false);
    }

    public static boolean avoidAdjacentBreaking(BlockStateInterface bsi, int x, int y, int z, boolean directlyAbove) {
        BlockState state = bsi.get0(x, y, z);
        Block block = state.getBlock();
        if (!directlyAbove && block instanceof FallingBlock && ((Boolean)Itemics.settings().avoidUpdatingFallingBlocks.value).booleanValue() && FallingBlock.canFallThrough(bsi.get0(x, y - 1, z))) {
            return true;
        }
        if (block instanceof FlowingFluidBlock) {
            if (directlyAbove || ((Boolean)Itemics.settings().strictLiquidCheck.value).booleanValue()) {
                return true;
            }
            int level = state.get(FlowingFluidBlock.LEVEL);
            if (level == 0) {
                return true;
            }
            return !(bsi.get0(x, y - 1, z).getBlock() instanceof FlowingFluidBlock);
        }
        return !state.getFluidState().isEmpty();
    }

    public static boolean canWalkThrough(IPlayerContext ctx, BetterBlockPos pos) {
        return MovementHelper.canWalkThrough(new BlockStateInterface(ctx), pos.x, pos.y, pos.z);
    }

    public static boolean canWalkThrough(BlockStateInterface bsi, int x, int y, int z) {
        return MovementHelper.canWalkThrough(bsi, x, y, z, bsi.get0(x, y, z));
    }

    public static boolean canWalkThrough(CalculationContext context, int x, int y, int z, BlockState state) {
        return context.precomputedData.canWalkThrough(context.bsi, x, y, z, state);
    }

    public static boolean canWalkThrough(CalculationContext context, int x, int y, int z) {
        return context.precomputedData.canWalkThrough(context.bsi, x, y, z, context.get(x, y, z));
    }

    public static boolean canWalkThrough(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        Ternary canWalkThrough = MovementHelper.canWalkThroughBlockState(state);
        if (canWalkThrough == Ternary.YES) {
            return true;
        }
        if (canWalkThrough == Ternary.NO) {
            return false;
        }
        return MovementHelper.canWalkThroughPosition(bsi, x, y, z, state);
    }

    public static Ternary canWalkThroughBlockState(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof AirBlock) {
            return Ternary.YES;
        }
        if (block instanceof AbstractFireBlock || block == Blocks.TRIPWIRE || block == Blocks.COBWEB || block == Blocks.END_PORTAL || block == Blocks.COCOA || block instanceof AbstractSkullBlock || block == Blocks.BUBBLE_COLUMN || block instanceof ShulkerBoxBlock || block instanceof SlabBlock || block instanceof TrapDoorBlock || block == Blocks.HONEY_BLOCK || block == Blocks.END_ROD || block == Blocks.SWEET_BERRY_BUSH) {
            return Ternary.NO;
        }
        if (((List)Itemics.settings().blocksToAvoid.value).contains(block)) {
            return Ternary.NO;
        }
        if (block instanceof DoorBlock || block instanceof FenceGateBlock) {
            if (block == Blocks.IRON_DOOR) {
                return Ternary.NO;
            }
            return Ternary.YES;
        }
        if (block instanceof CarpetBlock) {
            return Ternary.MAYBE;
        }
        if (block instanceof SnowBlock) {
            return Ternary.MAYBE;
        }
        FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty()) {
            if (fluidState.getFluid().getLevel(fluidState) != 8) {
                return Ternary.NO;
            }
            return Ternary.MAYBE;
        }
        if (block instanceof CauldronBlock) {
            return Ternary.NO;
        }
        try {
            if (state.allowsMovement(null, null, PathType.LAND)) {
                return Ternary.YES;
            }
            return Ternary.NO;
        }
        catch (Throwable exception) {
            System.out.println("The block " + state.getBlock().getTranslatedName().getString() + " requires a special case due to the exception " + exception.getMessage());
            return Ternary.MAYBE;
        }
    }

    public static boolean canWalkThroughPosition(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CarpetBlock) {
            return MovementHelper.canWalkOn(bsi, x, y - 1, z);
        }
        if (block instanceof SnowBlock) {
            if (!bsi.worldContainsLoadedChunk(x, z)) {
                return true;
            }
            if (state.get(SnowBlock.LAYERS) >= 3) {
                return false;
            }
            return MovementHelper.canWalkOn(bsi, x, y - 1, z);
        }
        FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty()) {
            if (MovementHelper.isFlowing(x, y, z, state, bsi)) {
                return false;
            }
            if (((Boolean)Itemics.settings().assumeWalkOnWater.value).booleanValue()) {
                return false;
            }
            BlockState up = bsi.get0(x, y + 1, z);
            if (!up.getFluidState().isEmpty() || up.getBlock() instanceof LilyPadBlock) {
                return false;
            }
            return fluidState.getFluid() instanceof WaterFluid;
        }
        return state.allowsMovement(bsi.access, BlockPos.ZERO, PathType.LAND);
    }

    public static Ternary fullyPassableBlockState(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof AirBlock) {
            return Ternary.YES;
        }
        if (block instanceof AbstractFireBlock || block == Blocks.TRIPWIRE || block == Blocks.COBWEB || block == Blocks.VINE || block == Blocks.LADDER || block == Blocks.COCOA || block instanceof DoorBlock || block instanceof FenceGateBlock || block instanceof SnowBlock || !state.getFluidState().isEmpty() || block instanceof TrapDoorBlock || block instanceof EndPortalBlock || block instanceof SkullBlock || block instanceof ShulkerBoxBlock) {
            return Ternary.NO;
        }
        try {
            if (state.allowsMovement(null, null, PathType.LAND)) {
                return Ternary.YES;
            }
            return Ternary.NO;
        }
        catch (Throwable exception) {
            System.out.println("The block " + state.getBlock().getTranslatedName().getString() + " requires a special case due to the exception " + exception.getMessage());
            return Ternary.MAYBE;
        }
    }

    public static boolean fullyPassable(CalculationContext context, int x, int y, int z) {
        return MovementHelper.fullyPassable(context, x, y, z, context.get(x, y, z));
    }

    public static boolean fullyPassable(CalculationContext context, int x, int y, int z, BlockState state) {
        return context.precomputedData.fullyPassable(context.bsi, x, y, z, state);
    }

    public static boolean fullyPassable(IPlayerContext ctx, BlockPos pos) {
        BlockState state = ctx.world().getBlockState(pos);
        Ternary fullyPassable = MovementHelper.fullyPassableBlockState(state);
        if (fullyPassable == Ternary.YES) {
            return true;
        }
        if (fullyPassable == Ternary.NO) {
            return false;
        }
        return MovementHelper.fullyPassablePosition(new BlockStateInterface(ctx), pos.getX(), pos.getY(), pos.getZ(), state);
    }

    public static boolean fullyPassablePosition(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        return state.allowsMovement(bsi.access, bsi.isPassableBlockPos.setPos(x, y, z), PathType.LAND);
    }

    public static boolean isReplaceable(int x, int y, int z, BlockState state, BlockStateInterface bsi) {
        Block block = state.getBlock();
        if (block instanceof AirBlock) {
            return true;
        }
        if (block instanceof SnowBlock) {
            if (!bsi.worldContainsLoadedChunk(x, z)) {
                return true;
            }
            return state.get(SnowBlock.LAYERS) == 1;
        }
        if (block == Blocks.LARGE_FERN || block == Blocks.TALL_GRASS) {
            return true;
        }
        return state.getMaterial().isReplaceable();
    }

    @Deprecated
    public static boolean isReplacable(int x, int y, int z, BlockState state, BlockStateInterface bsi) {
        return MovementHelper.isReplaceable(x, y, z, state, bsi);
    }

    public static boolean isDoorPassable(IPlayerContext ctx, BlockPos doorPos, BlockPos playerPos) {
        if (playerPos.equals(doorPos)) {
            return false;
        }
        BlockState state = BlockStateInterface.get(ctx, doorPos);
        if (!(state.getBlock() instanceof DoorBlock)) {
            return true;
        }
        return MovementHelper.isHorizontalBlockPassable(doorPos, state, playerPos, DoorBlock.OPEN);
    }

    public static boolean isGatePassable(IPlayerContext ctx, BlockPos gatePos, BlockPos playerPos) {
        if (playerPos.equals(gatePos)) {
            return false;
        }
        BlockState state = BlockStateInterface.get(ctx, gatePos);
        if (!(state.getBlock() instanceof FenceGateBlock)) {
            return true;
        }
        return state.get(FenceGateBlock.OPEN);
    }

    public static boolean isHorizontalBlockPassable(BlockPos blockPos, BlockState blockState, BlockPos playerPos, BooleanProperty propertyOpen) {
        Direction.Axis playerFacing;
        if (playerPos.equals(blockPos)) {
            return false;
        }
        Direction.Axis facing = blockState.get(HorizontalBlock.HORIZONTAL_FACING).getAxis();
        boolean open = blockState.get(propertyOpen);
        if (playerPos.north().equals(blockPos) || playerPos.south().equals(blockPos)) {
            playerFacing = Direction.Axis.Z;
        } else if (playerPos.east().equals(blockPos) || playerPos.west().equals(blockPos)) {
            playerFacing = Direction.Axis.X;
        } else {
            return true;
        }
        return facing == playerFacing == open;
    }

    public static boolean avoidWalkingInto(BlockState state) {
        Block block = state.getBlock();
        return !state.getFluidState().isEmpty() || block == Blocks.MAGMA_BLOCK || block == Blocks.CACTUS || block == Blocks.SWEET_BERRY_BUSH || block instanceof AbstractFireBlock || block == Blocks.END_PORTAL || block == Blocks.COBWEB || block == Blocks.BUBBLE_COLUMN;
    }

    public static boolean canWalkOn(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        Ternary canWalkOn = MovementHelper.canWalkOnBlockState(state);
        if (canWalkOn == Ternary.YES) {
            return true;
        }
        if (canWalkOn == Ternary.NO) {
            return false;
        }
        return MovementHelper.canWalkOnPosition(bsi, x, y, z, state);
    }

    public static Ternary canWalkOnBlockState(BlockState state) {
        Block block = state.getBlock();
        if (MovementHelper.isBlockNormalCube(state) && block != Blocks.MAGMA_BLOCK && block != Blocks.BUBBLE_COLUMN && block != Blocks.HONEY_BLOCK) {
            return Ternary.YES;
        }
        if (block == Blocks.LADDER || block == Blocks.VINE && ((Boolean)Itemics.settings().allowVines.value).booleanValue()) {
            return Ternary.YES;
        }
        if (block == Blocks.FARMLAND || block == Blocks.GRASS_PATH) {
            return Ternary.YES;
        }
        if (block == Blocks.ENDER_CHEST || block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
            return Ternary.YES;
        }
        if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
            return Ternary.YES;
        }
        if (block instanceof StairsBlock) {
            return Ternary.YES;
        }
        if (MovementHelper.isWater(state)) {
            return Ternary.MAYBE;
        }
        if (MovementHelper.isLava(state) && ((Boolean)Itemics.settings().assumeWalkOnLava.value).booleanValue()) {
            return Ternary.MAYBE;
        }
        if (block instanceof SlabBlock) {
            if (!((Boolean)Itemics.settings().allowWalkOnBottomSlab.value).booleanValue()) {
                if (state.get(SlabBlock.TYPE) != SlabType.BOTTOM) {
                    return Ternary.YES;
                }
                return Ternary.NO;
            }
            return Ternary.YES;
        }
        return Ternary.NO;
    }

    public static boolean canWalkOnPosition(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        Block block = state.getBlock();
        if (MovementHelper.isWater(state)) {
            BlockState upState = bsi.get0(x, y + 1, z);
            Block up = upState.getBlock();
            if (up == Blocks.LILY_PAD || up instanceof CarpetBlock) {
                return true;
            }
            if (MovementHelper.isFlowing(x, y, z, state, bsi) || upState.getFluidState().getFluid() == Fluids.FLOWING_WATER) {
                return MovementHelper.isWater(upState) && (Boolean)Itemics.settings().assumeWalkOnWater.value == false;
            }
            return MovementHelper.isWater(upState) ^ (Boolean)Itemics.settings().assumeWalkOnWater.value;
        }
        return MovementHelper.isLava(state) && !MovementHelper.isFlowing(x, y, z, state, bsi) && (Boolean)Itemics.settings().assumeWalkOnLava.value != false;
    }

    public static boolean canWalkOn(CalculationContext context, int x, int y, int z, BlockState state) {
        return context.precomputedData.canWalkOn(context.bsi, x, y, z, state);
    }

    public static boolean canWalkOn(CalculationContext context, int x, int y, int z) {
        return MovementHelper.canWalkOn(context, x, y, z, context.get(x, y, z));
    }

    public static boolean canWalkOn(IPlayerContext ctx, BetterBlockPos pos, BlockState state) {
        return MovementHelper.canWalkOn(new BlockStateInterface(ctx), pos.x, pos.y, pos.z, state);
    }

    public static boolean canWalkOn(IPlayerContext ctx, BlockPos pos) {
        return MovementHelper.canWalkOn(new BlockStateInterface(ctx), pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean canWalkOn(IPlayerContext ctx, BetterBlockPos pos) {
        return MovementHelper.canWalkOn(new BlockStateInterface(ctx), pos.x, pos.y, pos.z);
    }

    public static boolean canWalkOn(BlockStateInterface bsi, int x, int y, int z) {
        return MovementHelper.canWalkOn(bsi, x, y, z, bsi.get0(x, y, z));
    }

    public static boolean canUseFrostWalker(CalculationContext context, BlockState state) {
        return context.frostWalker != 0 && state.getMaterial() == Material.WATER && state.get(FlowingFluidBlock.LEVEL) == 0;
    }

    public static boolean canUseFrostWalker(IPlayerContext ctx, BlockPos pos) {
        BlockState state = BlockStateInterface.get(ctx, pos);
        return EnchantmentHelper.hasFrostWalker(ctx.player()) && state.getMaterial() == Material.WATER && state.get(FlowingFluidBlock.LEVEL) == 0;
    }

    public static boolean mustBeSolidToWalkOn(CalculationContext context, int x, int y, int z, BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.LADDER || block == Blocks.VINE) {
            return false;
        }
        if (!state.getFluidState().isEmpty()) {
            if (block instanceof SlabBlock) {
                if (state.get(SlabBlock.TYPE) != SlabType.BOTTOM) {
                    return true;
                }
            } else if (block instanceof StairsBlock) {
                if (state.get(StairsBlock.HALF) == Half.TOP) {
                    return true;
                }
                StairsShape shape = state.get(StairsBlock.SHAPE);
                if (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT) {
                    return true;
                }
            } else if (block instanceof TrapDoorBlock ? state.get(TrapDoorBlock.OPEN) == false && state.get(TrapDoorBlock.HALF) == Half.TOP : block == Blocks.SCAFFOLDING) {
                return true;
            }
            if (context.assumeWalkOnWater) {
                return false;
            }
            Block blockAbove = context.getBlock(x, y + 1, z);
            if (blockAbove instanceof FlowingFluidBlock) {
                return false;
            }
        }
        return true;
    }

    public static boolean canPlaceAgainst(BlockStateInterface bsi, int x, int y, int z) {
        return MovementHelper.canPlaceAgainst(bsi, x, y, z, bsi.get0(x, y, z));
    }

    public static boolean canPlaceAgainst(BlockStateInterface bsi, BlockPos pos) {
        return MovementHelper.canPlaceAgainst(bsi, pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean canPlaceAgainst(IPlayerContext ctx, BlockPos pos) {
        return MovementHelper.canPlaceAgainst(new BlockStateInterface(ctx), pos);
    }

    public static boolean canPlaceAgainst(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        if (!bsi.worldBorder.canPlaceAt(x, z)) {
            return false;
        }
        return MovementHelper.isBlockNormalCube(state) || state.getBlock() == Blocks.GLASS || state.getBlock() instanceof StainedGlassBlock;
    }

    public static double getMiningDurationTicks(CalculationContext context, int x, int y, int z, boolean includeFalling) {
        return MovementHelper.getMiningDurationTicks(context, x, y, z, context.get(x, y, z), includeFalling);
    }

    public static double getMiningDurationTicks(CalculationContext context, int x, int y, int z, BlockState state, boolean includeFalling) {
        Block block = state.getBlock();
        if (!MovementHelper.canWalkThrough(context, x, y, z, state)) {
            BlockState above;
            if (!state.getFluidState().isEmpty()) {
                return 1000000.0;
            }
            double mult = context.breakCostMultiplierAt(x, y, z, state);
            if (mult >= 1000000.0) {
                return 1000000.0;
            }
            if (MovementHelper.avoidBreaking(context.bsi, x, y, z, state)) {
                return 1000000.0;
            }
            double strVsBlock = context.toolSet.getStrVsBlock(state);
            if (strVsBlock <= 0.0) {
                return 1000000.0;
            }
            double result = 1.0 / strVsBlock;
            result += context.breakBlockAdditionalCost;
            result *= mult;
            if (includeFalling && (above = context.get(x, y + 1, z)).getBlock() instanceof FallingBlock) {
                result += MovementHelper.getMiningDurationTicks(context, x, y + 1, z, above, true);
            }
            return result;
        }
        return 0.0;
    }

    public static boolean isBottomSlab(BlockState state) {
        return state.getBlock() instanceof SlabBlock && state.get(SlabBlock.TYPE) == SlabType.BOTTOM;
    }

    public static void switchToBestToolFor(IPlayerContext ctx, BlockState b) {
        MovementHelper.switchToBestToolFor(ctx, b, new ToolSet(ctx.player()), (Boolean)ItemicsAPI.getSettings().preferSilkTouch.value);
    }

    public static void switchToBestToolFor(IPlayerContext ctx, BlockState b, ToolSet ts, boolean preferSilkTouch) {
        if (((Boolean)Itemics.settings().autoTool.value).booleanValue() && !((Boolean)Itemics.settings().assumeExternalAutoTool.value).booleanValue()) {
            ctx.player().inventory.currentItem = ts.getBestSlot(b.getBlock(), preferSilkTouch);
        }
    }

    public static void moveTowards(IPlayerContext ctx, MovementState state, BlockPos pos) {
        state.setTarget(new MovementState.MovementTarget(new Rotation(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), VecUtils.getBlockPosCenter(pos), ctx.playerRotations()).getYaw(), ctx.player().packetPitch), false)).setInput(Input.MOVE_FORWARD, true);
    }

    public static boolean isWater(BlockState state) {
        Fluid f = state.getFluidState().getFluid();
        return f == Fluids.WATER || f == Fluids.FLOWING_WATER;
    }

    public static boolean isWater(IPlayerContext ctx, BlockPos bp) {
        return MovementHelper.isWater(BlockStateInterface.get(ctx, bp));
    }

    public static boolean isLava(BlockState state) {
        Fluid f = state.getFluidState().getFluid();
        return f == Fluids.LAVA || f == Fluids.FLOWING_LAVA;
    }

    public static boolean isLiquid(IPlayerContext ctx, BlockPos p) {
        return MovementHelper.isLiquid(BlockStateInterface.get(ctx, p));
    }

    public static boolean isLiquid(BlockState blockState) {
        return !blockState.getFluidState().isEmpty();
    }

    public static boolean possiblyFlowing(BlockState state) {
        FluidState fluidState = state.getFluidState();
        return fluidState.getFluid() instanceof FlowingFluid && fluidState.getFluid().getLevel(fluidState) != 8;
    }

    public static boolean isFlowing(int x, int y, int z, BlockState state, BlockStateInterface bsi) {
        FluidState fluidState = state.getFluidState();
        if (!(fluidState.getFluid() instanceof FlowingFluid)) {
            return false;
        }
        if (fluidState.getFluid().getLevel(fluidState) != 8) {
            return true;
        }
        return MovementHelper.possiblyFlowing(bsi.get0(x + 1, y, z)) || MovementHelper.possiblyFlowing(bsi.get0(x - 1, y, z)) || MovementHelper.possiblyFlowing(bsi.get0(x, y, z + 1)) || MovementHelper.possiblyFlowing(bsi.get0(x, y, z - 1));
    }

    public static boolean isBlockNormalCube(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof BambooBlock || block instanceof MovingPistonBlock || block instanceof ScaffoldingBlock || block instanceof ShulkerBoxBlock) {
            return false;
        }
        try {
            return Block.isOpaque(state.getCollisionShape(null, null));
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static PlaceResult attemptToPlaceABlock(MovementState state, IItemics itemics, BlockPos placeAt, boolean preferDown, boolean wouldSneak) {
        IPlayerContext ctx = itemics.getPlayerContext();
        Optional<Rotation> direct = RotationUtils.reachable(ctx, placeAt, wouldSneak);
        boolean found = false;
        if (direct.isPresent()) {
            state.setTarget(new MovementState.MovementTarget(direct.get(), true));
            found = true;
        }
        for (int i = 0; i < 5; ++i) {
            BlockPos against1 = placeAt.offset(Movement.HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP[i]);
            if (!MovementHelper.canPlaceAgainst(ctx, against1)) continue;
            if (!((Itemics)itemics).getInventoryBehavior().selectThrowawayForLocation(false, placeAt.getX(), placeAt.getY(), placeAt.getZ())) {
                Helper.HELPER.logDebug("bb pls get me some blocks. dirt, netherrack, cobble");
                state.setStatus(MovementStatus.UNREACHABLE);
                return PlaceResult.NO_OPTION;
            }
            double faceX = ((double)(placeAt.getX() + against1.getX()) + 1.0) * 0.5;
            double faceY = ((double)(placeAt.getY() + against1.getY()) + 0.5) * 0.5;
            double faceZ = ((double)(placeAt.getZ() + against1.getZ()) + 1.0) * 0.5;
            Rotation place = RotationUtils.calcRotationFromVec3d(wouldSneak ? RayTraceUtils.inferSneakingEyePosition(ctx.player()) : ctx.playerHead(), new Vector3d(faceX, faceY, faceZ), ctx.playerRotations());
            RayTraceResult res = RayTraceUtils.rayTraceTowards(ctx.player(), place, ctx.playerController().getBlockReachDistance(), wouldSneak);
            if (res == null || res.getType() != RayTraceResult.Type.BLOCK || !((BlockRayTraceResult)res).getPos().equals(against1) || !((BlockRayTraceResult)res).getPos().offset(((BlockRayTraceResult)res).getFace()).equals(placeAt)) continue;
            state.setTarget(new MovementState.MovementTarget(place, true));
            found = true;
            if (!preferDown) break;
        }
        if (ctx.getSelectedBlock().isPresent()) {
            BlockPos selectedBlock = ctx.getSelectedBlock().get();
            Direction side = ((BlockRayTraceResult)ctx.objectMouseOver()).getFace();
            if (selectedBlock.equals(placeAt) || MovementHelper.canPlaceAgainst(ctx, selectedBlock) && selectedBlock.offset(side).equals(placeAt)) {
                if (wouldSneak) {
                    state.setInput(Input.SNEAK, true);
                }
                ((Itemics)itemics).getInventoryBehavior().selectThrowawayForLocation(true, placeAt.getX(), placeAt.getY(), placeAt.getZ());
                return PlaceResult.READY_TO_PLACE;
            }
        }
        if (found) {
            if (wouldSneak) {
                state.setInput(Input.SNEAK, true);
            }
            ((Itemics)itemics).getInventoryBehavior().selectThrowawayForLocation(true, placeAt.getX(), placeAt.getY(), placeAt.getZ());
            return PlaceResult.ATTEMPTING;
        }
        return PlaceResult.NO_OPTION;
    }

    public static boolean isTransparent(Block b) {
        return b instanceof AirBlock || b == Blocks.LAVA || b == Blocks.WATER;
    }

    public static enum PlaceResult {
        READY_TO_PLACE,
        ATTEMPTING,
        NO_OPTION;

    }
}
