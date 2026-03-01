package dimasik.itemics.process;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import dimasik.itemics.api.pathing.goals.GoalComposite;
import dimasik.itemics.api.process.IFarmProcess;
import dimasik.itemics.api.process.PathingCommand;
import dimasik.itemics.api.process.PathingCommandType;
import dimasik.itemics.api.utils.RayTraceUtils;
import dimasik.itemics.api.utils.Rotation;
import dimasik.itemics.api.utils.RotationUtils;
import dimasik.itemics.api.utils.input.Input;
import dimasik.itemics.api.utils.interfaces.IGoalRenderPos;
import dimasik.itemics.cache.WorldScanner;
import dimasik.itemics.pathing.movement.MovementHelper;
import dimasik.itemics.process.BuilderProcess;
import dimasik.itemics.utils.ItemicsProcessHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class FarmProcess
extends ItemicsProcessHelper
implements IFarmProcess {
    private boolean active;
    private List<BlockPos> locations;
    private int tickCount;
    private int range;
    private BlockPos center;
    private static final List<Item> FARMLAND_PLANTABLE = Arrays.asList(Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.POTATO, Items.CARROT);
    private static final List<Item> PICKUP_DROPPED = Arrays.asList(Items.BEETROOT_SEEDS, Items.BEETROOT, Items.MELON_SEEDS, Items.MELON_SLICE, Blocks.MELON.asItem(), Items.WHEAT_SEEDS, Items.WHEAT, Items.PUMPKIN_SEEDS, Blocks.PUMPKIN.asItem(), Items.POTATO, Items.CARROT, Items.NETHER_WART, Blocks.SUGAR_CANE.asItem(), Blocks.CACTUS.asItem());

    public FarmProcess(Itemics itemics) {
        super(itemics);
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void farm(int range, BlockPos pos) {
        this.center = pos == null ? this.itemics.getPlayerContext().playerFeet() : pos;
        this.range = range;
        this.active = true;
        this.locations = null;
    }

    private boolean readyForHarvest(World world, BlockPos pos, BlockState state) {
        for (Harvest harvest : Harvest.values()) {
            if (harvest.block != state.getBlock()) continue;
            return harvest.readyToHarvest(world, pos, state);
        }
        return false;
    }

    private boolean isPlantable(ItemStack stack) {
        return FARMLAND_PLANTABLE.contains(stack.getItem());
    }

    private boolean isBoneMeal(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().equals(Items.BONE_MEAL);
    }

    private boolean isNetherWart(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().equals(Items.NETHER_WART);
    }

    @Override
    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
        ArrayList<Block> scan = new ArrayList<Block>();
        for (Harvest harvest : Harvest.values()) {
            scan.add(harvest.block);
        }
        if (((Boolean)Itemics.settings().replantCrops.value).booleanValue()) {
            scan.add(Blocks.FARMLAND);
            if (((Boolean)Itemics.settings().replantNetherWart.value).booleanValue()) {
                scan.add(Blocks.SOUL_SAND);
            }
        }
        if ((Integer)Itemics.settings().mineGoalUpdateInterval.value != 0 && this.tickCount++ % (Integer)Itemics.settings().mineGoalUpdateInterval.value == 0) {
            Itemics.getExecutor().execute(() -> {
                this.locations = WorldScanner.INSTANCE.scanChunkRadius(this.ctx, scan, 256, 10, 10);
            });
        }
        if (this.locations == null) {
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
        }
        ArrayList<BlockPos> toBreak = new ArrayList<BlockPos>();
        ArrayList<BlockPos> openFarmland = new ArrayList<BlockPos>();
        ArrayList<BlockPos> bonemealable = new ArrayList<BlockPos>();
        ArrayList<BlockPos> openSoulsand = new ArrayList<BlockPos>();
        for (BlockPos blockPos : this.locations) {
            IGrowable ig;
            if (this.range != 0 && blockPos.distanceSq(this.center) > (double)(this.range * this.range)) continue;
            BlockState blockState = this.ctx.world().getBlockState(blockPos);
            boolean bl = this.ctx.world().getBlockState(blockPos.up()).getBlock() instanceof AirBlock;
            if (blockState.getBlock() == Blocks.FARMLAND) {
                if (!bl) continue;
                openFarmland.add(blockPos);
                continue;
            }
            if (blockState.getBlock() == Blocks.SOUL_SAND) {
                if (!bl) continue;
                openSoulsand.add(blockPos);
                continue;
            }
            if (this.readyForHarvest(this.ctx.world(), blockPos, blockState)) {
                toBreak.add(blockPos);
                continue;
            }
            if (!(blockState.getBlock() instanceof IGrowable) || !(ig = (IGrowable)((Object)blockState.getBlock())).canGrow(this.ctx.world(), blockPos, blockState, true) || !ig.canUseBonemeal(this.ctx.world(), this.ctx.world().rand, blockPos, blockState)) continue;
            bonemealable.add(blockPos);
        }
        this.itemics.getInputOverrideHandler().clearAllKeys();
        for (BlockPos blockPos : toBreak) {
            Optional<Rotation> optional = RotationUtils.reachable(this.ctx, blockPos);
            if (!optional.isPresent() || !isSafeToCancel) continue;
            this.itemics.getLookBehavior().updateTarget(optional.get(), true);
            MovementHelper.switchToBestToolFor(this.ctx, this.ctx.world().getBlockState(blockPos));
            if (this.ctx.isLookingAt(blockPos)) {
                this.itemics.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
            }
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
        }
        ArrayList<BlockPos> both = new ArrayList<BlockPos>(openFarmland);
        both.addAll(openSoulsand);
        for (BlockPos blockPos : both) {
            RayTraceResult result;
            boolean bl = openSoulsand.contains(blockPos);
            Optional<Rotation> rot = RotationUtils.reachableOffset(this.ctx.player(), blockPos, new Vector3d((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5), this.ctx.playerController().getBlockReachDistance(), false);
            if (!rot.isPresent() || !isSafeToCancel || !this.itemics.getInventoryBehavior().throwaway(true, bl ? this::isNetherWart : this::isPlantable) || !((result = RayTraceUtils.rayTraceTowards(this.ctx.player(), rot.get(), this.ctx.playerController().getBlockReachDistance())) instanceof BlockRayTraceResult) || ((BlockRayTraceResult)result).getFace() != Direction.UP) continue;
            this.itemics.getLookBehavior().updateTarget(rot.get(), true);
            if (this.ctx.isLookingAt(blockPos)) {
                this.itemics.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
            }
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
        }
        for (BlockPos blockPos : bonemealable) {
            Optional<Rotation> optional = RotationUtils.reachable(this.ctx, blockPos);
            if (!optional.isPresent() || !isSafeToCancel || !this.itemics.getInventoryBehavior().throwaway(true, this::isBoneMeal)) continue;
            this.itemics.getLookBehavior().updateTarget(optional.get(), true);
            if (this.ctx.isLookingAt(blockPos)) {
                this.itemics.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
            }
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
        }
        if (calcFailed) {
            this.logDirect("Farm failed");
            if (((Boolean)Itemics.settings().notificationOnFarmFail.value).booleanValue()) {
                this.logNotification("Farm failed", true);
            }
            this.onLostControl();
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
        }
        ArrayList<IGoalRenderPos> arrayList = new ArrayList<IGoalRenderPos>();
        for (BlockPos blockPos : toBreak) {
            arrayList.add(new BuilderProcess.GoalBreak(blockPos));
        }
        if (this.itemics.getInventoryBehavior().throwaway(false, this::isPlantable)) {
            for (BlockPos blockPos : openFarmland) {
                arrayList.add(new GoalBlock(blockPos.up()));
            }
        }
        if (this.itemics.getInventoryBehavior().throwaway(false, this::isNetherWart)) {
            for (BlockPos blockPos : openSoulsand) {
                arrayList.add(new GoalBlock(blockPos.up()));
            }
        }
        if (this.itemics.getInventoryBehavior().throwaway(false, this::isBoneMeal)) {
            for (BlockPos blockPos : bonemealable) {
                arrayList.add(new GoalBlock(blockPos));
            }
        }
        for (Entity entity : this.ctx.entities()) {
            ItemEntity ei;
            if (!(entity instanceof ItemEntity) || !entity.isOnGround() || !PICKUP_DROPPED.contains((ei = (ItemEntity)entity).getItem().getItem())) continue;
            arrayList.add(new GoalBlock(new BlockPos(entity.getPositionVec().x, entity.getPositionVec().y + 0.1, entity.getPositionVec().z)));
        }
        return new PathingCommand(new GoalComposite(arrayList.toArray(new Goal[0])), PathingCommandType.SET_GOAL_AND_PATH);
    }

    @Override
    public void onLostControl() {
        this.active = false;
    }

    @Override
    public String displayName0() {
        return "Farming";
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    private static enum Harvest {
        WHEAT((CropsBlock)Blocks.WHEAT),
        CARROTS((CropsBlock)Blocks.CARROTS),
        POTATOES((CropsBlock)Blocks.POTATOES),
        BEETROOT((CropsBlock)Blocks.BEETROOTS),
        PUMPKIN(Blocks.PUMPKIN, state -> true),
        MELON(Blocks.MELON, state -> true),
        NETHERWART(Blocks.NETHER_WART, state -> state.get(NetherWartBlock.AGE) >= 3),
        SUGARCANE(Blocks.SUGAR_CANE, null){

            @Override
            public boolean readyToHarvest(World world, BlockPos pos, BlockState state) {
                if (((Boolean)Itemics.settings().replantCrops.value).booleanValue()) {
                    return world.getBlockState(pos.down()).getBlock() instanceof SugarCaneBlock;
                }
                return true;
            }
        }
        ,
        CACTUS(Blocks.CACTUS, null){

            @Override
            public boolean readyToHarvest(World world, BlockPos pos, BlockState state) {
                if (((Boolean)Itemics.settings().replantCrops.value).booleanValue()) {
                    return world.getBlockState(pos.down()).getBlock() instanceof CactusBlock;
                }
                return true;
            }
        };

        public final Block block;
        public final Predicate<BlockState> readyToHarvest;

        private Harvest(CropsBlock blockCrops) {
            this(blockCrops, blockCrops::isMaxAge);
        }

        private Harvest(Block block, Predicate<BlockState> readyToHarvest) {
            this.block = block;
            this.readyToHarvest = readyToHarvest;
        }

        public boolean readyToHarvest(World world, BlockPos pos, BlockState state) {
            return this.readyToHarvest.test(state);
        }
    }
}
