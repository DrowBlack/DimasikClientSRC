package dimasik.itemics.process;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dimasik.itemics.Itemics;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import dimasik.itemics.api.pathing.goals.GoalComposite;
import dimasik.itemics.api.pathing.goals.GoalGetToBlock;
import dimasik.itemics.api.process.IBuilderProcess;
import dimasik.itemics.api.process.PathingCommand;
import dimasik.itemics.api.process.PathingCommandType;
import dimasik.itemics.api.schematic.FillSchematic;
import dimasik.itemics.api.schematic.ISchematic;
import dimasik.itemics.api.schematic.IStaticSchematic;
import dimasik.itemics.api.schematic.SubstituteSchematic;
import dimasik.itemics.api.schematic.format.ISchematicFormat;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.RayTraceUtils;
import dimasik.itemics.api.utils.Rotation;
import dimasik.itemics.api.utils.RotationUtils;
import dimasik.itemics.api.utils.input.Input;
import dimasik.itemics.pathing.movement.CalculationContext;
import dimasik.itemics.pathing.movement.Movement;
import dimasik.itemics.pathing.movement.MovementHelper;
import dimasik.itemics.utils.BlockStateInterface;
import dimasik.itemics.utils.ItemicsProcessHelper;
import dimasik.itemics.utils.PathingCommandContext;
import dimasik.itemics.utils.schematic.MapArtSchematic;
import dimasik.itemics.utils.schematic.SchematicSystem;
import dimasik.itemics.utils.schematic.SelectionSchematic;
import dimasik.itemics.utils.schematic.format.defaults.LitematicaSchematic;
import dimasik.itemics.utils.schematic.litematica.LitematicaHelper;
import dimasik.itemics.utils.schematic.schematica.SchematicaHelper;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public final class BuilderProcess
extends ItemicsProcessHelper
implements IBuilderProcess {
    private HashSet<BetterBlockPos> incorrectPositions;
    private LongOpenHashSet observedCompleted;
    private String name;
    private ISchematic realSchematic;
    private ISchematic schematic;
    private Vector3i origin;
    private int ticks;
    private boolean paused;
    private int layer;
    private int numRepeats;
    private List<BlockState> approxPlaceable;
    public int stopAtHeight = 0;
    public static final Set<Property<?>> orientationProps = ImmutableSet.of(RotatedPillarBlock.AXIS, HorizontalBlock.HORIZONTAL_FACING, StairsBlock.FACING, StairsBlock.HALF, StairsBlock.SHAPE, PaneBlock.NORTH, new Property[]{PaneBlock.EAST, PaneBlock.SOUTH, PaneBlock.WEST, VineBlock.UP, TrapDoorBlock.OPEN, TrapDoorBlock.HALF});

    public BuilderProcess(Itemics itemics) {
        super(itemics);
    }

    @Override
    public void build(String name, ISchematic schematic, Vector3i origin) {
        this.name = name;
        this.schematic = schematic;
        this.realSchematic = null;
        boolean buildingSelectionSchematic = schematic instanceof SelectionSchematic;
        if (!((Map)Itemics.settings().buildSubstitutes.value).isEmpty()) {
            this.schematic = new SubstituteSchematic(this.schematic, (Map)Itemics.settings().buildSubstitutes.value);
        }
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        if (((Boolean)Itemics.settings().schematicOrientationX.value).booleanValue()) {
            x += schematic.widthX();
        }
        if (((Boolean)Itemics.settings().schematicOrientationY.value).booleanValue()) {
            y += schematic.heightY();
        }
        if (((Boolean)Itemics.settings().schematicOrientationZ.value).booleanValue()) {
            z += schematic.lengthZ();
        }
        this.origin = new Vector3i(x, y, z);
        this.paused = false;
        this.layer = (Integer)Itemics.settings().startAtLayer.value;
        this.stopAtHeight = schematic.heightY();
        if (((Boolean)Itemics.settings().buildOnlySelection.value).booleanValue() && buildingSelectionSchematic) {
            if (this.itemics.getSelectionManager().getSelections().length == 0) {
                this.logDirect("Poor little kitten forgot to set a selection while BuildOnlySelection is true");
                this.stopAtHeight = 0;
            } else if (((Boolean)Itemics.settings().buildInLayers.value).booleanValue()) {
                OptionalInt minim = Stream.of(this.itemics.getSelectionManager().getSelections()).mapToInt(sel -> sel.min().y).min();
                OptionalInt maxim = Stream.of(this.itemics.getSelectionManager().getSelections()).mapToInt(sel -> sel.max().y).max();
                if (minim.isPresent() && maxim.isPresent()) {
                    int startAtHeight = (Boolean)Itemics.settings().layerOrder.value != false ? y + schematic.heightY() - maxim.getAsInt() : minim.getAsInt() - y;
                    this.stopAtHeight = ((Boolean)Itemics.settings().layerOrder.value != false ? y + schematic.heightY() - minim.getAsInt() : maxim.getAsInt() - y) + 1;
                    this.layer = Math.max(this.layer, startAtHeight / (Integer)Itemics.settings().layerHeight.value);
                    this.logDebug(String.format("Schematic starts at y=%s with height %s", y, schematic.heightY()));
                    this.logDebug(String.format("Selection starts at y=%s and ends at y=%s", minim.getAsInt(), maxim.getAsInt()));
                    this.logDebug(String.format("Considering relevant height %s - %s", startAtHeight, this.stopAtHeight));
                }
            }
        }
        this.numRepeats = 0;
        this.observedCompleted = new LongOpenHashSet();
        this.incorrectPositions = null;
    }

    @Override
    public void resume() {
        this.paused = false;
    }

    @Override
    public void pause() {
        this.paused = true;
    }

    @Override
    public boolean isPaused() {
        return this.paused;
    }

    @Override
    public boolean build(String name, File schematic, Vector3i origin) {
        ISchematic parsed;
        Optional<ISchematicFormat> format = SchematicSystem.INSTANCE.getByFile(schematic);
        if (!format.isPresent()) {
            return false;
        }
        try {
            parsed = format.get().parse(new FileInputStream(schematic));
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (((Boolean)Itemics.settings().mapArtMode.value).booleanValue()) {
            parsed = new MapArtSchematic((IStaticSchematic)parsed);
        }
        if (((Boolean)Itemics.settings().buildOnlySelection.value).booleanValue()) {
            parsed = new SelectionSchematic(parsed, origin, this.itemics.getSelectionManager().getSelections());
        }
        this.build(name, parsed, origin);
        return true;
    }

    @Override
    public void buildOpenSchematic() {
        if (SchematicaHelper.isSchematicaPresent()) {
            Optional<Tuple<IStaticSchematic, BlockPos>> schematic = SchematicaHelper.getOpenSchematic();
            if (schematic.isPresent()) {
                ISchematic schem;
                IStaticSchematic s = schematic.get().getA();
                BlockPos origin = schematic.get().getB();
                ISchematic iSchematic = schem = (Boolean)Itemics.settings().mapArtMode.value != false ? new MapArtSchematic(s) : s;
                if (((Boolean)Itemics.settings().buildOnlySelection.value).booleanValue()) {
                    schem = new SelectionSchematic(schem, origin, this.itemics.getSelectionManager().getSelections());
                }
                this.build(schematic.get().getA().toString(), schem, (Vector3i)origin);
            } else {
                this.logDirect("No schematic currently open");
            }
        } else {
            this.logDirect("Schematica is not present");
        }
    }

    @Override
    public void buildOpenLitematic(int i) {
        if (LitematicaHelper.isLitematicaPresent()) {
            if (LitematicaHelper.hasLoadedSchematic()) {
                String name = LitematicaHelper.getName(i);
                try {
                    LitematicaSchematic schematic1 = new LitematicaSchematic(CompressedStreamTools.readCompressed(Files.newInputStream(LitematicaHelper.getSchematicFile(i).toPath(), new OpenOption[0])), false);
                    Vector3i correctedOrigin = LitematicaHelper.getCorrectedOrigin(schematic1, i);
                    LitematicaSchematic schematic2 = LitematicaHelper.blackMagicFuckery(schematic1, i);
                    this.build(name, schematic2, correctedOrigin);
                }
                catch (Exception e) {
                    this.logDirect("Schematic File could not be loaded.");
                }
            } else {
                this.logDirect("No schematic currently loaded");
            }
        } else {
            this.logDirect("Litematica is not present");
        }
    }

    @Override
    public void clearArea(BlockPos corner1, BlockPos corner2) {
        BlockPos origin = new BlockPos(Math.min(corner1.getX(), corner2.getX()), Math.min(corner1.getY(), corner2.getY()), Math.min(corner1.getZ(), corner2.getZ()));
        int widthX = Math.abs(corner1.getX() - corner2.getX()) + 1;
        int heightY = Math.abs(corner1.getY() - corner2.getY()) + 1;
        int lengthZ = Math.abs(corner1.getZ() - corner2.getZ()) + 1;
        this.build("clear area", new FillSchematic(widthX, heightY, lengthZ, Blocks.AIR.getDefaultState()), (Vector3i)origin);
    }

    @Override
    public List<BlockState> getApproxPlaceable() {
        return new ArrayList<BlockState>(this.approxPlaceable);
    }

    @Override
    public boolean isActive() {
        return this.schematic != null;
    }

    public BlockState placeAt(int x, int y, int z, BlockState current) {
        if (!this.isActive()) {
            return null;
        }
        if (!this.schematic.inSchematic(x - this.origin.getX(), y - this.origin.getY(), z - this.origin.getZ(), current)) {
            return null;
        }
        BlockState state = this.schematic.desiredState(x - this.origin.getX(), y - this.origin.getY(), z - this.origin.getZ(), current, this.approxPlaceable);
        if (state.getBlock() instanceof AirBlock) {
            return null;
        }
        return state;
    }

    private Optional<Tuple<BetterBlockPos, Rotation>> toBreakNearPlayer(BuilderCalculationContext bcc) {
        BetterBlockPos center = this.ctx.playerFeet();
        BetterBlockPos pathStart = this.itemics.getPathingBehavior().pathStart();
        for (int dx = -5; dx <= 5; ++dx) {
            int dy;
            int n = dy = (Boolean)Itemics.settings().breakFromAbove.value != false ? -1 : 0;
            while (dy <= 5) {
                for (int dz = -5; dz <= 5; ++dz) {
                    BlockState curr;
                    BlockState desired;
                    int x = center.x + dx;
                    int y = center.y + dy;
                    int z = center.z + dz;
                    if (dy == -1 && x == pathStart.x && z == pathStart.z || (desired = bcc.getSchematic(x, y, z, bcc.bsi.get0(x, y, z))) == null || (curr = bcc.bsi.get0(x, y, z)).getBlock() instanceof AirBlock || curr.getBlock() == Blocks.WATER || curr.getBlock() == Blocks.LAVA || this.valid(curr, desired, false)) continue;
                    BetterBlockPos pos = new BetterBlockPos(x, y, z);
                    Optional<Rotation> rot = RotationUtils.reachable(this.ctx.player(), (BlockPos)pos, this.ctx.playerController().getBlockReachDistance());
                    if (!rot.isPresent()) continue;
                    return Optional.of(new Tuple<BetterBlockPos, Rotation>(pos, rot.get()));
                }
                ++dy;
            }
        }
        return Optional.empty();
    }

    private Optional<Placement> searchForPlacables(BuilderCalculationContext bcc, List<BlockState> desirableOnHotbar) {
        BetterBlockPos center = this.ctx.playerFeet();
        for (int dx = -5; dx <= 5; ++dx) {
            for (int dy = -5; dy <= 1; ++dy) {
                for (int dz = -5; dz <= 5; ++dz) {
                    BlockState curr;
                    int x = center.x + dx;
                    int y = center.y + dy;
                    int z = center.z + dz;
                    BlockState desired = bcc.getSchematic(x, y, z, bcc.bsi.get0(x, y, z));
                    if (desired == null || !MovementHelper.isReplaceable(x, y, z, curr = bcc.bsi.get0(x, y, z), bcc.bsi) || this.valid(curr, desired, false) || dy == 1 && bcc.bsi.get0(x, y + 1, z).getBlock() instanceof AirBlock) continue;
                    desirableOnHotbar.add(desired);
                    Optional<Placement> opt = this.possibleToPlace(desired, x, y, z, bcc.bsi);
                    if (!opt.isPresent()) continue;
                    return opt;
                }
            }
        }
        return Optional.empty();
    }

    public boolean placementPlausible(BlockPos pos, BlockState state) {
        VoxelShape voxelshape = state.getCollisionShape(this.ctx.world(), pos);
        return voxelshape.isEmpty() || this.ctx.world().checkNoEntityCollision(null, voxelshape.withOffset(pos.getX(), pos.getY(), pos.getZ()));
    }

    private Optional<Placement> possibleToPlace(BlockState toPlace, int x, int y, int z, BlockStateInterface bsi) {
        for (Direction against : Direction.values()) {
            BetterBlockPos placeAgainstPos = new BetterBlockPos(x, y, z).offset(against);
            BlockState placeAgainstState = bsi.get0(placeAgainstPos);
            if (MovementHelper.isReplaceable(placeAgainstPos.x, placeAgainstPos.y, placeAgainstPos.z, placeAgainstState, bsi) || !toPlace.isValidPosition(this.ctx.world(), new BetterBlockPos(x, y, z)) || !this.placementPlausible(new BetterBlockPos(x, y, z), toPlace)) continue;
            AxisAlignedBB aabb = placeAgainstState.getShape(this.ctx.world(), placeAgainstPos).getBoundingBox();
            for (Vector3d placementMultiplier : BuilderProcess.aabbSideMultipliers(against)) {
                OptionalInt hotbar;
                double placeX = (double)placeAgainstPos.x + aabb.minX * placementMultiplier.x + aabb.maxX * (1.0 - placementMultiplier.x);
                double placeY = (double)placeAgainstPos.y + aabb.minY * placementMultiplier.y + aabb.maxY * (1.0 - placementMultiplier.y);
                double placeZ = (double)placeAgainstPos.z + aabb.minZ * placementMultiplier.z + aabb.maxZ * (1.0 - placementMultiplier.z);
                Rotation rot = RotationUtils.calcRotationFromVec3d(RayTraceUtils.inferSneakingEyePosition(this.ctx.player()), new Vector3d(placeX, placeY, placeZ), this.ctx.playerRotations());
                RayTraceResult result = RayTraceUtils.rayTraceTowards(this.ctx.player(), rot, this.ctx.playerController().getBlockReachDistance(), true);
                if (result == null || result.getType() != RayTraceResult.Type.BLOCK || !((BlockRayTraceResult)result).getPos().equals(placeAgainstPos) || ((BlockRayTraceResult)result).getFace() != against.getOpposite() || !(hotbar = this.hasAnyItemThatWouldPlace(toPlace, result, rot)).isPresent()) continue;
                return Optional.of(new Placement(hotbar.getAsInt(), placeAgainstPos, against.getOpposite(), rot));
            }
        }
        return Optional.empty();
    }

    private OptionalInt hasAnyItemThatWouldPlace(BlockState desired, RayTraceResult result, Rotation rot) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = this.ctx.player().inventory.mainInventory.get(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) continue;
            float originalYaw = this.ctx.player().packetYaw;
            float originalPitch = this.ctx.player().packetPitch;
            this.ctx.player().packetYaw = rot.getYaw();
            this.ctx.player().packetPitch = rot.getPitch();
            BlockItemUseContext meme = new BlockItemUseContext(new ItemUseContext(this.ctx.world(), this.ctx.player(), Hand.MAIN_HAND, stack, (BlockRayTraceResult)result){});
            BlockState wouldBePlaced = ((BlockItem)stack.getItem()).getBlock().getStateForPlacement(meme);
            this.ctx.player().packetYaw = originalYaw;
            this.ctx.player().packetPitch = originalPitch;
            if (wouldBePlaced == null || !meme.canPlace() || !this.valid(wouldBePlaced, desired, true)) continue;
            return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    private static Vector3d[] aabbSideMultipliers(Direction side) {
        switch (side) {
            case UP: {
                return new Vector3d[]{new Vector3d(0.5, 1.0, 0.5), new Vector3d(0.1, 1.0, 0.5), new Vector3d(0.9, 1.0, 0.5), new Vector3d(0.5, 1.0, 0.1), new Vector3d(0.5, 1.0, 0.9)};
            }
            case DOWN: {
                return new Vector3d[]{new Vector3d(0.5, 0.0, 0.5), new Vector3d(0.1, 0.0, 0.5), new Vector3d(0.9, 0.0, 0.5), new Vector3d(0.5, 0.0, 0.1), new Vector3d(0.5, 0.0, 0.9)};
            }
            case NORTH: 
            case SOUTH: 
            case EAST: 
            case WEST: {
                double x = side.getXOffset() == 0 ? 0.5 : (double)(1 + side.getXOffset()) / 2.0;
                double z = side.getZOffset() == 0 ? 0.5 : (double)(1 + side.getZOffset()) / 2.0;
                return new Vector3d[]{new Vector3d(x, 0.25, z), new Vector3d(x, 0.75, z)};
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
        return this.onTick(calcFailed, isSafeToCancel, 0);
    }

    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel, int recursions) {
        Goal goal;
        Optional<Tuple<BetterBlockPos, Rotation>> toBreak;
        BuilderCalculationContext bcc;
        if (recursions > 1000) {
            return new PathingCommand(null, PathingCommandType.SET_GOAL_AND_PATH);
        }
        this.approxPlaceable = this.approxPlaceable(36);
        this.ticks = this.itemics.getInputOverrideHandler().isInputForcedDown(Input.CLICK_LEFT) ? 5 : --this.ticks;
        this.itemics.getInputOverrideHandler().clearAllKeys();
        if (this.paused) {
            return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
        }
        if (((Boolean)Itemics.settings().buildInLayers.value).booleanValue()) {
            int minYInclusive;
            int maxYInclusive;
            if (this.realSchematic == null) {
                this.realSchematic = this.schematic;
            }
            final ISchematic realSchematic = this.realSchematic;
            if (((Boolean)Itemics.settings().layerOrder.value).booleanValue()) {
                maxYInclusive = realSchematic.heightY() - 1;
                minYInclusive = realSchematic.heightY() - this.layer * (Integer)Itemics.settings().layerHeight.value;
            } else {
                maxYInclusive = this.layer * (Integer)Itemics.settings().layerHeight.value - 1;
                minYInclusive = 0;
            }
            this.schematic = new ISchematic(){

                @Override
                public BlockState desiredState(int x, int y, int z, BlockState current, List<BlockState> approxPlaceable) {
                    return realSchematic.desiredState(x, y, z, current, BuilderProcess.this.approxPlaceable);
                }

                @Override
                public boolean inSchematic(int x, int y, int z, BlockState currentState) {
                    return ISchematic.super.inSchematic(x, y, z, currentState) && y >= minYInclusive && y <= maxYInclusive && realSchematic.inSchematic(x, y, z, currentState);
                }

                @Override
                public void reset() {
                    realSchematic.reset();
                }

                @Override
                public int widthX() {
                    return realSchematic.widthX();
                }

                @Override
                public int heightY() {
                    return realSchematic.heightY();
                }

                @Override
                public int lengthZ() {
                    return realSchematic.lengthZ();
                }
            };
        }
        if (!this.recalc(bcc = new BuilderCalculationContext())) {
            if (((Boolean)Itemics.settings().buildInLayers.value).booleanValue() && this.layer * (Integer)Itemics.settings().layerHeight.value < this.stopAtHeight) {
                this.logDirect("Starting layer " + this.layer);
                ++this.layer;
                return this.onTick(calcFailed, isSafeToCancel, recursions + 1);
            }
            Vector3i repeat = (Vector3i)Itemics.settings().buildRepeat.value;
            int max = (Integer)Itemics.settings().buildRepeatCount.value;
            ++this.numRepeats;
            if (repeat.equals(new Vector3i(0, 0, 0)) || max != -1 && this.numRepeats >= max) {
                this.logDirect("Done building");
                if (((Boolean)Itemics.settings().notificationOnBuildFinished.value).booleanValue()) {
                    this.logNotification("Done building", false);
                }
                this.onLostControl();
                return null;
            }
            this.layer = 0;
            this.origin = new BlockPos(this.origin).add(repeat);
            if (!((Boolean)Itemics.settings().buildRepeatSneaky.value).booleanValue()) {
                this.schematic.reset();
            }
            this.logDirect("Repeating build in vector " + String.valueOf(repeat) + ", new origin is " + String.valueOf(this.origin));
            return this.onTick(calcFailed, isSafeToCancel, recursions + 1);
        }
        if (((Boolean)Itemics.settings().distanceTrim.value).booleanValue()) {
            this.trim();
        }
        if ((toBreak = this.toBreakNearPlayer(bcc)).isPresent() && isSafeToCancel && this.ctx.player().isOnGround()) {
            Rotation rot = toBreak.get().getB();
            BetterBlockPos pos = toBreak.get().getA();
            this.itemics.getLookBehavior().updateTarget(rot, true);
            MovementHelper.switchToBestToolFor(this.ctx, bcc.get(pos));
            if (this.ctx.player().isCrouching()) {
                this.itemics.getInputOverrideHandler().setInputForceState(Input.SNEAK, true);
            }
            if (this.ctx.isLookingAt(pos) || this.ctx.playerRotations().isReallyCloseTo(rot)) {
                this.itemics.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
            }
            return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
        }
        ArrayList<BlockState> desirableOnHotbar = new ArrayList<BlockState>();
        Optional<Placement> toPlace = this.searchForPlacables(bcc, desirableOnHotbar);
        if (toPlace.isPresent() && isSafeToCancel && this.ctx.player().isOnGround() && this.ticks <= 0) {
            Rotation rot = toPlace.get().rot;
            this.itemics.getLookBehavior().updateTarget(rot, true);
            this.ctx.player().inventory.currentItem = toPlace.get().hotbarSelection;
            this.itemics.getInputOverrideHandler().setInputForceState(Input.SNEAK, true);
            if (this.ctx.isLookingAt(toPlace.get().placeAgainst) && ((BlockRayTraceResult)this.ctx.objectMouseOver()).getFace().equals(toPlace.get().side) || this.ctx.playerRotations().isReallyCloseTo(rot)) {
                this.itemics.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
            }
            return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
        }
        if (((Boolean)Itemics.settings().allowInventory.value).booleanValue()) {
            ArrayList<Integer> usefulSlots = new ArrayList<Integer>();
            ArrayList<BlockState> noValidHotbarOption = new ArrayList<BlockState>();
            block0: for (BlockState desired : desirableOnHotbar) {
                for (int i = 0; i < 9; ++i) {
                    if (!this.valid(this.approxPlaceable.get(i), desired, true)) continue;
                    usefulSlots.add(i);
                    continue block0;
                }
                noValidHotbarOption.add(desired);
            }
            block2: for (int i = 9; i < 36; ++i) {
                for (BlockState desired : noValidHotbarOption) {
                    if (!this.valid(this.approxPlaceable.get(i), desired, true)) continue;
                    this.itemics.getInventoryBehavior().attemptToPutOnHotbar(i, usefulSlots::contains);
                    break block2;
                }
            }
        }
        if ((goal = this.assemble(bcc, this.approxPlaceable.subList(0, 9))) == null && (goal = this.assemble(bcc, this.approxPlaceable, true)) == null) {
            if (((Boolean)Itemics.settings().skipFailedLayers.value).booleanValue() && ((Boolean)Itemics.settings().buildInLayers.value).booleanValue() && this.layer * (Integer)Itemics.settings().layerHeight.value < this.realSchematic.heightY()) {
                this.logDirect("Skipping layer that I cannot construct! Layer #" + this.layer);
                ++this.layer;
                return this.onTick(calcFailed, isSafeToCancel, recursions + 1);
            }
            this.logDirect("Unable to do it. Pausing. resume to resume, cancel to cancel");
            this.paused = true;
            return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
        }
        return new PathingCommandContext(goal, PathingCommandType.FORCE_REVALIDATE_GOAL_AND_PATH, bcc);
    }

    private boolean recalc(BuilderCalculationContext bcc) {
        if (this.incorrectPositions == null) {
            this.incorrectPositions = new HashSet();
            this.fullRecalc(bcc);
            if (this.incorrectPositions.isEmpty()) {
                return false;
            }
        }
        this.recalcNearby(bcc);
        if (this.incorrectPositions.isEmpty()) {
            this.fullRecalc(bcc);
        }
        return !this.incorrectPositions.isEmpty();
    }

    private void trim() {
        HashSet<BetterBlockPos> copy = new HashSet<BetterBlockPos>(this.incorrectPositions);
        copy.removeIf(pos -> pos.distanceSq(this.ctx.player().getPosition()) > 200.0);
        if (!copy.isEmpty()) {
            this.incorrectPositions = copy;
        }
    }

    private void recalcNearby(BuilderCalculationContext bcc) {
        BetterBlockPos center = this.ctx.playerFeet();
        int radius = (Integer)Itemics.settings().builderTickScanRadius.value;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    int x = center.x + dx;
                    int y = center.y + dy;
                    int z = center.z + dz;
                    BlockState desired = bcc.getSchematic(x, y, z, bcc.bsi.get0(x, y, z));
                    if (desired == null) continue;
                    BetterBlockPos pos = new BetterBlockPos(x, y, z);
                    if (this.valid(bcc.bsi.get0(x, y, z), desired, false)) {
                        this.incorrectPositions.remove(pos);
                        this.observedCompleted.add(BetterBlockPos.longHash(pos));
                        continue;
                    }
                    this.incorrectPositions.add(pos);
                    this.observedCompleted.remove(BetterBlockPos.longHash(pos));
                }
            }
        }
    }

    private void fullRecalc(BuilderCalculationContext bcc) {
        this.incorrectPositions = new HashSet();
        for (int y = 0; y < this.schematic.heightY(); ++y) {
            for (int z = 0; z < this.schematic.lengthZ(); ++z) {
                for (int x = 0; x < this.schematic.widthX(); ++x) {
                    int blockZ;
                    int blockY;
                    int blockX = x + this.origin.getX();
                    BlockState current = bcc.bsi.get0(blockX, blockY = y + this.origin.getY(), blockZ = z + this.origin.getZ());
                    if (!this.schematic.inSchematic(x, y, z, current)) continue;
                    if (bcc.bsi.worldContainsLoadedChunk(blockX, blockZ)) {
                        if (this.valid(bcc.bsi.get0(blockX, blockY, blockZ), this.schematic.desiredState(x, y, z, current, this.approxPlaceable), false)) {
                            this.observedCompleted.add(BetterBlockPos.longHash(blockX, blockY, blockZ));
                            continue;
                        }
                        this.incorrectPositions.add(new BetterBlockPos(blockX, blockY, blockZ));
                        this.observedCompleted.remove(BetterBlockPos.longHash(blockX, blockY, blockZ));
                        if (this.incorrectPositions.size() <= (Integer)Itemics.settings().incorrectSize.value) continue;
                        return;
                    }
                    if (this.observedCompleted.contains(BetterBlockPos.longHash(blockX, blockY, blockZ)) || ((List)Itemics.settings().buildSkipBlocks.value).contains(this.schematic.desiredState(x, y, z, current, this.approxPlaceable).getBlock())) continue;
                    this.incorrectPositions.add(new BetterBlockPos(blockX, blockY, blockZ));
                    if (this.incorrectPositions.size() <= (Integer)Itemics.settings().incorrectSize.value) continue;
                    return;
                }
            }
        }
    }

    private Goal assemble(BuilderCalculationContext bcc, List<BlockState> approxPlaceable) {
        return this.assemble(bcc, approxPlaceable, false);
    }

    private Goal assemble(BuilderCalculationContext bcc, List<BlockState> approxPlaceable, boolean logMissing) {
        ArrayList placeable = new ArrayList();
        ArrayList breakable = new ArrayList();
        ArrayList sourceLiquids = new ArrayList();
        ArrayList flowingLiquids = new ArrayList();
        HashMap missing = new HashMap();
        this.incorrectPositions.forEach(pos -> {
            BlockState state = bcc.bsi.get0((BlockPos)pos);
            if (state.getBlock() instanceof AirBlock) {
                if (approxPlaceable.contains(bcc.getSchematic(pos.x, pos.y, pos.z, state))) {
                    placeable.add(pos);
                } else {
                    BlockState desired = bcc.getSchematic(pos.x, pos.y, pos.z, state);
                    missing.put(desired, 1 + missing.getOrDefault(desired, 0));
                }
            } else if (state.getBlock() instanceof FlowingFluidBlock) {
                if (!MovementHelper.possiblyFlowing(state)) {
                    sourceLiquids.add(pos);
                } else {
                    flowingLiquids.add(pos);
                }
            } else {
                breakable.add(pos);
            }
        });
        ArrayList toBreak = new ArrayList();
        breakable.forEach(pos -> toBreak.add(this.breakGoal((BlockPos)pos, bcc)));
        ArrayList toPlace = new ArrayList();
        placeable.forEach(pos -> {
            if (!placeable.contains(pos.down()) && !placeable.contains(pos.down(2))) {
                toPlace.add(this.placementGoal((BlockPos)pos, bcc));
            }
        });
        sourceLiquids.forEach(pos -> toPlace.add(new GoalBlock(pos.up())));
        if (!toPlace.isEmpty()) {
            return new JankyGoalComposite(new GoalComposite(toPlace.toArray(new Goal[0])), new GoalComposite(toBreak.toArray(new Goal[0])));
        }
        if (toBreak.isEmpty()) {
            if (logMissing && !missing.isEmpty()) {
                this.logDirect("Missing materials for at least:");
                this.logDirect(missing.entrySet().stream().map(e -> String.format("%sx %s", e.getValue(), e.getKey())).collect(Collectors.joining("\n")));
            }
            if (logMissing && !flowingLiquids.isEmpty()) {
                this.logDirect("Unreplaceable liquids at at least:");
                this.logDirect(flowingLiquids.stream().map(p -> String.format("%s %s %s", p.x, p.y, p.z)).collect(Collectors.joining("\n")));
            }
            return null;
        }
        return new GoalComposite(toBreak.toArray(new Goal[0]));
    }

    private Goal placementGoal(BlockPos pos, BuilderCalculationContext bcc) {
        if (!(this.ctx.world().getBlockState(pos).getBlock() instanceof AirBlock)) {
            return new GoalPlace(pos);
        }
        boolean allowSameLevel = !(this.ctx.world().getBlockState(pos.up()).getBlock() instanceof AirBlock);
        BlockState current = this.ctx.world().getBlockState(pos);
        for (Direction facing : Movement.HORIZONTALS_BUT_ALSO_DOWN_____SO_EVERY_DIRECTION_EXCEPT_UP) {
            if (!MovementHelper.canPlaceAgainst(this.ctx, pos.offset(facing)) || !this.placementPlausible(pos, bcc.getSchematic(pos.getX(), pos.getY(), pos.getZ(), current))) continue;
            return new GoalAdjacent(pos, pos.offset(facing), allowSameLevel);
        }
        return new GoalPlace(pos);
    }

    private Goal breakGoal(BlockPos pos, BuilderCalculationContext bcc) {
        if (((Boolean)Itemics.settings().goalBreakFromAbove.value).booleanValue() && bcc.bsi.get0(pos.up()).getBlock() instanceof AirBlock && bcc.bsi.get0(pos.up(2)).getBlock() instanceof AirBlock) {
            return new JankyGoalComposite(new GoalBreak(pos), new GoalGetToBlock(pos.up()){

                @Override
                public boolean isInGoal(int x, int y, int z) {
                    if (y > this.y || x == this.x && y == this.y && z == this.z) {
                        return false;
                    }
                    return super.isInGoal(x, y, z);
                }
            });
        }
        return new GoalBreak(pos);
    }

    @Override
    public void onLostControl() {
        this.incorrectPositions = null;
        this.name = null;
        this.schematic = null;
        this.realSchematic = null;
        this.layer = (Integer)Itemics.settings().startAtLayer.value;
        this.numRepeats = 0;
        this.paused = false;
        this.observedCompleted = null;
    }

    @Override
    public String displayName0() {
        return this.paused ? "Builder Paused" : "Building " + this.name;
    }

    private List<BlockState> approxPlaceable(int size) {
        ArrayList<BlockState> result = new ArrayList<BlockState>();
        for (int i = 0; i < size; ++i) {
            ItemStack stack = this.ctx.player().inventory.mainInventory.get(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
                result.add(Blocks.AIR.getDefaultState());
                continue;
            }
            BlockState itemState = ((BlockItem)stack.getItem()).getBlock().getStateForPlacement(new BlockItemUseContext(new ItemUseContext(this.ctx.world(), this.ctx.player(), Hand.MAIN_HAND, stack, new BlockRayTraceResult(new Vector3d(this.ctx.player().getPositionVec().x, this.ctx.player().getPositionVec().y, this.ctx.player().getPositionVec().z), Direction.UP, this.ctx.playerFeet(), false)){}));
            if (itemState != null) {
                result.add(itemState);
                continue;
            }
            result.add(Blocks.AIR.getDefaultState());
        }
        return result;
    }

    private boolean sameBlockstate(BlockState first, BlockState second) {
        if (first.getBlock() != second.getBlock()) {
            return false;
        }
        boolean ignoreDirection = (Boolean)Itemics.settings().buildIgnoreDirection.value;
        List ignoredProps = (List)Itemics.settings().buildIgnoreProperties.value;
        if (!ignoreDirection && ignoredProps.isEmpty()) {
            return first.equals(second);
        }
        ImmutableMap<Property<?>, Comparable<?>> map1 = first.getValues();
        ImmutableMap<Property<?>, Comparable<?>> map2 = second.getValues();
        for (Property prop : map1.keySet()) {
            if (map1.get(prop) == map2.get(prop) || ignoreDirection && orientationProps.contains(prop) || ignoredProps.contains(prop.getName())) continue;
            return false;
        }
        return true;
    }

    private boolean valid(BlockState current, BlockState desired, boolean itemVerify) {
        if (desired == null) {
            return true;
        }
        if (current.getBlock() instanceof FlowingFluidBlock && ((Boolean)Itemics.settings().okIfWater.value).booleanValue()) {
            return true;
        }
        if (current.getBlock() instanceof AirBlock && desired.getBlock() instanceof AirBlock) {
            return true;
        }
        if (current.getBlock() instanceof AirBlock && ((List)Itemics.settings().okIfAir.value).contains(desired.getBlock())) {
            return true;
        }
        if (desired.getBlock() instanceof AirBlock && ((List)Itemics.settings().buildIgnoreBlocks.value).contains(current.getBlock())) {
            return true;
        }
        if (!(current.getBlock() instanceof AirBlock) && ((Boolean)Itemics.settings().buildIgnoreExisting.value).booleanValue() && !itemVerify) {
            return true;
        }
        if (((List)Itemics.settings().buildSkipBlocks.value).contains(desired.getBlock()) && !itemVerify) {
            return true;
        }
        if (((Map)Itemics.settings().buildValidSubstitutes.value).getOrDefault(desired.getBlock(), Collections.emptyList()).contains(current.getBlock()) && !itemVerify) {
            return true;
        }
        if (current.equals(desired)) {
            return true;
        }
        return this.sameBlockstate(current, desired);
    }

    public class BuilderCalculationContext
    extends CalculationContext {
        private final List<BlockState> placeable;
        private final ISchematic schematic;
        private final int originX;
        private final int originY;
        private final int originZ;

        public BuilderCalculationContext() {
            super(BuilderProcess.this.itemics, true);
            this.placeable = BuilderProcess.this.approxPlaceable(9);
            this.schematic = BuilderProcess.this.schematic;
            this.originX = BuilderProcess.this.origin.getX();
            this.originY = BuilderProcess.this.origin.getY();
            this.originZ = BuilderProcess.this.origin.getZ();
            this.jumpPenalty += 10.0;
            this.backtrackCostFavoringCoefficient = 1.0;
        }

        private BlockState getSchematic(int x, int y, int z, BlockState current) {
            if (this.schematic.inSchematic(x - this.originX, y - this.originY, z - this.originZ, current)) {
                return this.schematic.desiredState(x - this.originX, y - this.originY, z - this.originZ, current, BuilderProcess.this.approxPlaceable);
            }
            return null;
        }

        @Override
        public double costOfPlacingAt(int x, int y, int z, BlockState current) {
            if (this.isPossiblyProtected(x, y, z) || !this.worldBorder.canPlaceAt(x, z)) {
                return 1000000.0;
            }
            BlockState sch = this.getSchematic(x, y, z, current);
            if (sch != null && !((List)Itemics.settings().buildSkipBlocks.value).contains(sch.getBlock())) {
                if (sch.getBlock() instanceof AirBlock) {
                    return this.placeBlockCost * 2.0;
                }
                if (this.placeable.contains(sch)) {
                    return 0.0;
                }
                if (!this.hasThrowaway) {
                    return 1000000.0;
                }
                return this.placeBlockCost * 3.0;
            }
            if (this.hasThrowaway) {
                return this.placeBlockCost;
            }
            return 1000000.0;
        }

        @Override
        public double breakCostMultiplierAt(int x, int y, int z, BlockState current) {
            if (!this.allowBreak && !this.allowBreakAnyway.contains(current.getBlock()) || this.isPossiblyProtected(x, y, z)) {
                return 1000000.0;
            }
            BlockState sch = this.getSchematic(x, y, z, current);
            if (sch != null && !((List)Itemics.settings().buildSkipBlocks.value).contains(sch.getBlock())) {
                if (sch.getBlock() instanceof AirBlock) {
                    return 1.0;
                }
                if (BuilderProcess.this.valid(this.bsi.get0(x, y, z), sch, false)) {
                    return (Double)Itemics.settings().breakCorrectBlockPenaltyMultiplier.value;
                }
                return 1.0;
            }
            return 1.0;
        }
    }

    public static class Placement {
        private final int hotbarSelection;
        private final BlockPos placeAgainst;
        private final Direction side;
        private final Rotation rot;

        public Placement(int hotbarSelection, BlockPos placeAgainst, Direction side, Rotation rot) {
            this.hotbarSelection = hotbarSelection;
            this.placeAgainst = placeAgainst;
            this.side = side;
            this.rot = rot;
        }
    }

    public static class JankyGoalComposite
    implements Goal {
        private final Goal primary;
        private final Goal fallback;

        public JankyGoalComposite(Goal primary, Goal fallback) {
            this.primary = primary;
            this.fallback = fallback;
        }

        @Override
        public boolean isInGoal(int x, int y, int z) {
            return this.primary.isInGoal(x, y, z) || this.fallback.isInGoal(x, y, z);
        }

        @Override
        public double heuristic(int x, int y, int z) {
            return this.primary.heuristic(x, y, z);
        }

        public String toString() {
            return "JankyComposite Primary: " + String.valueOf(this.primary) + " Fallback: " + String.valueOf(this.fallback);
        }
    }

    public static class GoalPlace
    extends GoalBlock {
        public GoalPlace(BlockPos placeAt) {
            super(placeAt.up());
        }

        @Override
        public double heuristic(int x, int y, int z) {
            return (double)(this.y * 100) + super.heuristic(x, y, z);
        }
    }

    public static class GoalAdjacent
    extends GoalGetToBlock {
        private boolean allowSameLevel;
        private BlockPos no;

        public GoalAdjacent(BlockPos pos, BlockPos no, boolean allowSameLevel) {
            super(pos);
            this.no = no;
            this.allowSameLevel = allowSameLevel;
        }

        @Override
        public boolean isInGoal(int x, int y, int z) {
            if (x == this.x && y == this.y && z == this.z) {
                return false;
            }
            if (x == this.no.getX() && y == this.no.getY() && z == this.no.getZ()) {
                return false;
            }
            if (!this.allowSameLevel && y == this.y - 1) {
                return false;
            }
            if (y < this.y - 1) {
                return false;
            }
            return super.isInGoal(x, y, z);
        }

        @Override
        public double heuristic(int x, int y, int z) {
            return (double)(this.y * 100) + super.heuristic(x, y, z);
        }
    }

    public static class GoalBreak
    extends GoalGetToBlock {
        public GoalBreak(BlockPos pos) {
            super(pos);
        }

        @Override
        public boolean isInGoal(int x, int y, int z) {
            if (y > this.y) {
                return false;
            }
            return super.isInGoal(x, y, z);
        }
    }
}
