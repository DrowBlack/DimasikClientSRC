package dimasik.itemics.pathing.path;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.pathing.calc.IPath;
import dimasik.itemics.api.pathing.movement.IMovement;
import dimasik.itemics.api.pathing.movement.MovementStatus;
import dimasik.itemics.api.pathing.path.IPathExecutor;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.api.utils.RotationUtils;
import dimasik.itemics.api.utils.VecUtils;
import dimasik.itemics.api.utils.input.Input;
import dimasik.itemics.behavior.PathingBehavior;
import dimasik.itemics.pathing.calc.AbstractNodeCostSearch;
import dimasik.itemics.pathing.movement.CalculationContext;
import dimasik.itemics.pathing.movement.Movement;
import dimasik.itemics.pathing.movement.MovementHelper;
import dimasik.itemics.pathing.movement.movements.MovementAscend;
import dimasik.itemics.pathing.movement.movements.MovementDescend;
import dimasik.itemics.pathing.movement.movements.MovementDiagonal;
import dimasik.itemics.pathing.movement.movements.MovementFall;
import dimasik.itemics.pathing.movement.movements.MovementParkour;
import dimasik.itemics.pathing.movement.movements.MovementTraverse;
import dimasik.itemics.pathing.path.CutoffPath;
import dimasik.itemics.pathing.path.SplicedPath;
import dimasik.itemics.utils.BlockStateInterface;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class PathExecutor
implements IPathExecutor,
Helper {
    private static final double MAX_MAX_DIST_FROM_PATH = 3.0;
    private static final double MAX_DIST_FROM_PATH = 2.0;
    private static final double MAX_TICKS_AWAY = 200.0;
    private final IPath path;
    private int pathPosition;
    private int ticksAway;
    private int ticksOnCurrent;
    private Double currentMovementOriginalCostEstimate;
    private Integer costEstimateIndex;
    private boolean failed;
    private boolean recalcBP = true;
    private HashSet<BlockPos> toBreak = new HashSet();
    private HashSet<BlockPos> toPlace = new HashSet();
    private HashSet<BlockPos> toWalkInto = new HashSet();
    private final PathingBehavior behavior;
    private final IPlayerContext ctx;
    private boolean sprintNextTick;

    public PathExecutor(PathingBehavior behavior, IPath path) {
        this.behavior = behavior;
        this.ctx = behavior.ctx;
        this.path = path;
        this.pathPosition = 0;
    }

    public boolean onTick() {
        double currentCost;
        Tuple<Double, BlockPos> status;
        if (this.pathPosition == this.path.length() - 1) {
            ++this.pathPosition;
        }
        if (this.pathPosition >= this.path.length()) {
            return true;
        }
        Movement movement = (Movement)this.path.movements().get(this.pathPosition);
        BetterBlockPos whereAmI = this.ctx.playerFeet();
        if (!movement.getValidPositions().contains(whereAmI)) {
            int i;
            for (i = 0; i < this.pathPosition && i < this.path.length(); ++i) {
                if (!((Movement)this.path.movements().get(i)).getValidPositions().contains(whereAmI)) continue;
                int previousPos = this.pathPosition;
                for (int j = this.pathPosition = i; j <= previousPos; ++j) {
                    this.path.movements().get(j).reset();
                }
                this.onChangeInPathPosition();
                this.onTick();
                return false;
            }
            for (i = this.pathPosition + 3; i < this.path.length() - 1; ++i) {
                if (!((Movement)this.path.movements().get(i)).getValidPositions().contains(whereAmI)) continue;
                if (i - this.pathPosition > 2) {
                    this.logDebug("Skipping forward " + (i - this.pathPosition) + " steps, to " + i);
                }
                this.pathPosition = i - 1;
                this.onChangeInPathPosition();
                this.onTick();
                return false;
            }
        }
        if (this.possiblyOffPath(status = this.closestPathPos(this.path), 2.0)) {
            ++this.ticksAway;
            System.out.println("FAR AWAY FROM PATH FOR " + this.ticksAway + " TICKS. Current distance: " + String.valueOf(status.getA()) + ". Threshold: 2.0");
            if ((double)this.ticksAway > 200.0) {
                this.logDebug("Too far away from path for too long, cancelling path");
                this.cancel();
                return false;
            }
        } else {
            this.ticksAway = 0;
        }
        if (this.possiblyOffPath(status, 3.0)) {
            this.logDebug("too far from path");
            this.cancel();
            return false;
        }
        BlockStateInterface bsi = new BlockStateInterface(this.ctx);
        for (int i = this.pathPosition - 10; i < this.pathPosition + 10; ++i) {
            if (i < 0 || i >= this.path.movements().size()) continue;
            Movement m = (Movement)this.path.movements().get(i);
            List<BlockPos> prevBreak = m.toBreak(bsi);
            List<BlockPos> prevPlace = m.toPlace(bsi);
            List<BlockPos> prevWalkInto = m.toWalkInto(bsi);
            m.resetBlockCache();
            if (!prevBreak.equals(m.toBreak(bsi))) {
                this.recalcBP = true;
            }
            if (!prevPlace.equals(m.toPlace(bsi))) {
                this.recalcBP = true;
            }
            if (prevWalkInto.equals(m.toWalkInto(bsi))) continue;
            this.recalcBP = true;
        }
        if (this.recalcBP) {
            HashSet<BlockPos> newBreak = new HashSet<BlockPos>();
            HashSet<BlockPos> newPlace = new HashSet<BlockPos>();
            HashSet<BlockPos> newWalkInto = new HashSet<BlockPos>();
            for (int i = this.pathPosition; i < this.path.movements().size(); ++i) {
                Movement m = (Movement)this.path.movements().get(i);
                newBreak.addAll(m.toBreak(bsi));
                newPlace.addAll(m.toPlace(bsi));
                newWalkInto.addAll(m.toWalkInto(bsi));
            }
            this.toBreak = newBreak;
            this.toPlace = newPlace;
            this.toWalkInto = newWalkInto;
            this.recalcBP = false;
        }
        if (this.pathPosition < this.path.movements().size() - 1) {
            IMovement next = this.path.movements().get(this.pathPosition + 1);
            if (!this.behavior.itemics.bsi.worldContainsLoadedChunk(next.getDest().x, next.getDest().z)) {
                this.logDebug("Pausing since destination is at edge of loaded chunks");
                this.clearKeys();
                return true;
            }
        }
        boolean canCancel = movement.safeToCancel();
        if (this.costEstimateIndex == null || this.costEstimateIndex != this.pathPosition) {
            this.costEstimateIndex = this.pathPosition;
            this.currentMovementOriginalCostEstimate = movement.getCost();
            for (int i = 1; i < (Integer)Itemics.settings().costVerificationLookahead.value && this.pathPosition + i < this.path.length() - 1; ++i) {
                if (!(((Movement)this.path.movements().get(this.pathPosition + i)).calculateCost(this.behavior.secretInternalGetCalculationContext()) >= 1000000.0) || !canCancel) continue;
                this.logDebug("Something has changed in the world and a future movement has become impossible. Cancelling.");
                this.cancel();
                return true;
            }
        }
        if ((currentCost = movement.recalculateCost(this.behavior.secretInternalGetCalculationContext())) >= 1000000.0 && canCancel) {
            this.logDebug("Something has changed in the world and this movement has become impossible. Cancelling.");
            this.cancel();
            return true;
        }
        if (!movement.calculatedWhileLoaded() && currentCost - this.currentMovementOriginalCostEstimate > (Double)Itemics.settings().maxCostIncrease.value && canCancel) {
            this.logDebug("Original cost " + this.currentMovementOriginalCostEstimate + " current cost " + currentCost + ". Cancelling.");
            this.cancel();
            return true;
        }
        if (this.shouldPause()) {
            this.logDebug("Pausing since current best path is a backtrack");
            this.clearKeys();
            return true;
        }
        MovementStatus movementStatus = movement.update();
        if (movementStatus == MovementStatus.UNREACHABLE || movementStatus == MovementStatus.FAILED) {
            this.logDebug("Movement returns status " + String.valueOf((Object)movementStatus));
            this.cancel();
            return true;
        }
        if (movementStatus == MovementStatus.SUCCESS) {
            ++this.pathPosition;
            this.onChangeInPathPosition();
            this.onTick();
            return true;
        }
        this.sprintNextTick = this.shouldSprintNextTick();
        if (!this.sprintNextTick) {
            this.ctx.player().setSprinting(false);
        }
        ++this.ticksOnCurrent;
        if ((double)this.ticksOnCurrent > this.currentMovementOriginalCostEstimate + (double)((Integer)Itemics.settings().movementTimeoutTicks.value).intValue()) {
            this.logDebug("This movement has taken too long (" + this.ticksOnCurrent + " ticks, expected " + this.currentMovementOriginalCostEstimate + "). Cancelling.");
            this.cancel();
            return true;
        }
        return canCancel;
    }

    private Tuple<Double, BlockPos> closestPathPos(IPath path) {
        double best = -1.0;
        BlockPos bestPos = null;
        for (IMovement movement : path.movements()) {
            for (BlockPos blockPos : ((Movement)movement).getValidPositions()) {
                double dist = VecUtils.entityDistanceToCenter(this.ctx.player(), blockPos);
                if (!(dist < best) && best != -1.0) continue;
                best = dist;
                bestPos = blockPos;
            }
        }
        return new Tuple<Double, Object>(best, bestPos);
    }

    private boolean shouldPause() {
        Optional<AbstractNodeCostSearch> current = this.behavior.getInProgress();
        if (!current.isPresent()) {
            return false;
        }
        if (!this.ctx.player().isOnGround()) {
            return false;
        }
        if (!MovementHelper.canWalkOn(this.ctx, this.ctx.playerFeet().down())) {
            return false;
        }
        if (!MovementHelper.canWalkThrough(this.ctx, this.ctx.playerFeet()) || !MovementHelper.canWalkThrough(this.ctx, this.ctx.playerFeet().up())) {
            return false;
        }
        if (!this.path.movements().get(this.pathPosition).safeToCancel()) {
            return false;
        }
        Optional<IPath> currentBest = current.get().bestPathSoFar();
        if (!currentBest.isPresent()) {
            return false;
        }
        List<BetterBlockPos> positions = currentBest.get().positions();
        if (positions.size() < 3) {
            return false;
        }
        positions = positions.subList(1, positions.size());
        return positions.contains(this.ctx.playerFeet());
    }

    private boolean possiblyOffPath(Tuple<Double, BlockPos> status, double leniency) {
        double distanceFromPath = status.getA();
        if (distanceFromPath > leniency) {
            if (this.path.movements().get(this.pathPosition) instanceof MovementFall) {
                BlockPos fallDest = this.path.positions().get(this.pathPosition + 1);
                return VecUtils.entityFlatDistanceToCenter(this.ctx.player(), fallDest) >= leniency;
            }
            return true;
        }
        return false;
    }

    public boolean snipsnapifpossible() {
        if (!this.ctx.player().isOnGround() && this.ctx.world().getFluidState(this.ctx.playerFeet()).isEmpty()) {
            return false;
        }
        if (this.ctx.player().getMotion().y < -0.1) {
            return false;
        }
        int index = this.path.positions().indexOf(this.ctx.playerFeet());
        if (index == -1) {
            return false;
        }
        this.pathPosition = index;
        this.clearKeys();
        return true;
    }

    private boolean shouldSprintNextTick() {
        Tuple<Vector3d, BlockPos> data;
        IMovement next;
        boolean requested = this.behavior.itemics.getInputOverrideHandler().isInputForcedDown(Input.SPRINT);
        this.behavior.itemics.getInputOverrideHandler().setInputForceState(Input.SPRINT, false);
        if (!new CalculationContext((IItemics)this.behavior.itemics, (boolean)false).canSprint) {
            return false;
        }
        IMovement current = this.path.movements().get(this.pathPosition);
        if (current instanceof MovementTraverse && this.pathPosition < this.path.length() - 3 && (next = this.path.movements().get(this.pathPosition + 1)) instanceof MovementAscend && PathExecutor.sprintableAscend(this.ctx, (MovementTraverse)current, (MovementAscend)next, this.path.movements().get(this.pathPosition + 2))) {
            if (PathExecutor.skipNow(this.ctx, current)) {
                this.logDebug("Skipping traverse to straight ascend");
                ++this.pathPosition;
                this.onChangeInPathPosition();
                this.onTick();
                this.behavior.itemics.getInputOverrideHandler().setInputForceState(Input.JUMP, true);
                return true;
            }
            this.logDebug("Too far to the side to safely sprint ascend");
        }
        if (requested) {
            return true;
        }
        if (current instanceof MovementDescend) {
            if (this.pathPosition < this.path.length() - 2 && MovementHelper.canUseFrostWalker(this.ctx, (next = this.path.movements().get(this.pathPosition + 1)).getDest().down()) && (next instanceof MovementTraverse || next instanceof MovementParkour)) {
                boolean sameFlatDirection;
                boolean couldPlaceInstead = (Boolean)Itemics.settings().allowPlace.value != false && this.behavior.itemics.getInventoryBehavior().hasGenericThrowaway() && next instanceof MovementParkour;
                boolean bl = sameFlatDirection = !current.getDirection().up().add(next.getDirection()).equals(BlockPos.ZERO) && current.getDirection().up().crossProduct(next.getDirection()).equals(BlockPos.ZERO);
                if (sameFlatDirection && !couldPlaceInstead) {
                    ((MovementDescend)current).forceSafeMode();
                }
            }
            if (((MovementDescend)current).safeMode() && !((MovementDescend)current).skipToAscend()) {
                this.logDebug("Sprinting would be unsafe");
                return false;
            }
            if (this.pathPosition < this.path.length() - 2) {
                next = this.path.movements().get(this.pathPosition + 1);
                if (next instanceof MovementAscend && current.getDirection().up().equals(next.getDirection().down())) {
                    ++this.pathPosition;
                    this.onChangeInPathPosition();
                    this.onTick();
                    this.logDebug("Skipping descend to straight ascend");
                    return true;
                }
                if (PathExecutor.canSprintFromDescendInto(this.ctx, current, next)) {
                    IMovement next_next;
                    if (next instanceof MovementDescend && this.pathPosition < this.path.length() - 3 && (next_next = this.path.movements().get(this.pathPosition + 2)) instanceof MovementDescend && !PathExecutor.canSprintFromDescendInto(this.ctx, next, next_next)) {
                        return false;
                    }
                    if (this.ctx.playerFeet().equals(current.getDest())) {
                        ++this.pathPosition;
                        this.onChangeInPathPosition();
                        this.onTick();
                    }
                    return true;
                }
            }
        }
        if (current instanceof MovementAscend && this.pathPosition != 0) {
            BetterBlockPos center;
            IMovement prev = this.path.movements().get(this.pathPosition - 1);
            if (prev instanceof MovementDescend && prev.getDirection().up().equals(current.getDirection().down()) && this.ctx.player().getPositionVec().y >= (double)(center = current.getSrc().up()).getY() - 0.07) {
                this.behavior.itemics.getInputOverrideHandler().setInputForceState(Input.JUMP, false);
                return true;
            }
            if (this.pathPosition < this.path.length() - 2 && prev instanceof MovementTraverse && PathExecutor.sprintableAscend(this.ctx, (MovementTraverse)prev, (MovementAscend)current, this.path.movements().get(this.pathPosition + 1))) {
                return true;
            }
        }
        if (current instanceof MovementFall && (data = this.overrideFall((MovementFall)current)) != null) {
            BetterBlockPos fallDest = new BetterBlockPos(data.getB());
            if (!this.path.positions().contains(fallDest)) {
                throw new IllegalStateException();
            }
            if (this.ctx.playerFeet().equals(fallDest)) {
                this.pathPosition = this.path.positions().indexOf(fallDest);
                this.onChangeInPathPosition();
                this.onTick();
                return true;
            }
            this.clearKeys();
            this.behavior.itemics.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), data.getA(), this.ctx.playerRotations()), false);
            this.behavior.itemics.getInputOverrideHandler().setInputForceState(Input.MOVE_FORWARD, true);
            return true;
        }
        return false;
    }

    private Tuple<Vector3d, BlockPos> overrideFall(MovementFall movement) {
        IMovement next;
        int i;
        BlockPos dir = movement.getDirection();
        if (dir.getY() < -3) {
            return null;
        }
        if (!movement.toBreakCached.isEmpty()) {
            return null;
        }
        Vector3i flatDir = new Vector3i(dir.getX(), 0, dir.getZ());
        block0: for (i = this.pathPosition + 1; i < this.path.length() - 1 && i < this.pathPosition + 3 && (next = this.path.movements().get(i)) instanceof MovementTraverse && flatDir.equals(next.getDirection()); ++i) {
            for (int y = next.getDest().y; y <= movement.getSrc().y + 1; ++y) {
                BlockPos chk = new BlockPos(next.getDest().x, y, next.getDest().z);
                if (!MovementHelper.fullyPassable(this.ctx, chk)) break block0;
            }
            if (!MovementHelper.canWalkOn(this.ctx, next.getDest().down())) break;
        }
        if (--i == this.pathPosition) {
            return null;
        }
        double len = (double)(i - this.pathPosition) - 0.4;
        return new Tuple<Vector3d, BlockPos>(new Vector3d((double)flatDir.getX() * len + (double)movement.getDest().x + 0.5, movement.getDest().y, (double)flatDir.getZ() * len + (double)movement.getDest().z + 0.5), movement.getDest().add(flatDir.getX() * (i - this.pathPosition), 0, flatDir.getZ() * (i - this.pathPosition)));
    }

    private static boolean skipNow(IPlayerContext ctx, IMovement current) {
        double offTarget = Math.abs((double)current.getDirection().getX() * ((double)current.getSrc().z + 0.5 - ctx.player().getPositionVec().z)) + Math.abs((double)current.getDirection().getZ() * ((double)current.getSrc().x + 0.5 - ctx.player().getPositionVec().x));
        if (offTarget > 0.1) {
            return false;
        }
        BlockPos headBonk = current.getSrc().subtract(current.getDirection()).up(2);
        if (MovementHelper.fullyPassable(ctx, headBonk)) {
            return true;
        }
        double flatDist = Math.abs((double)current.getDirection().getX() * ((double)headBonk.getX() + 0.5 - ctx.player().getPositionVec().x)) + Math.abs((double)current.getDirection().getZ() * ((double)headBonk.getZ() + 0.5 - ctx.player().getPositionVec().z));
        return flatDist > 0.8;
    }

    private static boolean sprintableAscend(IPlayerContext ctx, MovementTraverse current, MovementAscend next, IMovement nextnext) {
        if (!((Boolean)Itemics.settings().sprintAscends.value).booleanValue()) {
            return false;
        }
        if (!current.getDirection().equals(next.getDirection().down())) {
            return false;
        }
        if (nextnext.getDirection().getX() != next.getDirection().getX() || nextnext.getDirection().getZ() != next.getDirection().getZ()) {
            return false;
        }
        if (!MovementHelper.canWalkOn(ctx, current.getDest().down())) {
            return false;
        }
        if (!MovementHelper.canWalkOn(ctx, next.getDest().down())) {
            return false;
        }
        if (!next.toBreakCached.isEmpty()) {
            return false;
        }
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 3; ++y) {
                BlockPos chk = current.getSrc().up(y);
                if (x == 1) {
                    chk = chk.add(current.getDirection());
                }
                if (MovementHelper.fullyPassable(ctx, chk)) continue;
                return false;
            }
        }
        if (MovementHelper.avoidWalkingInto(ctx.world().getBlockState(current.getSrc().up(3)))) {
            return false;
        }
        return !MovementHelper.avoidWalkingInto(ctx.world().getBlockState(next.getDest().up(2)));
    }

    private static boolean canSprintFromDescendInto(IPlayerContext ctx, IMovement current, IMovement next) {
        if (next instanceof MovementDescend && next.getDirection().equals(current.getDirection())) {
            return true;
        }
        if (!MovementHelper.canWalkOn(ctx, current.getDest().add(current.getDirection()))) {
            return false;
        }
        if (next instanceof MovementTraverse && next.getDirection().down().equals(current.getDirection())) {
            return true;
        }
        return next instanceof MovementDiagonal && (Boolean)Itemics.settings().allowOvershootDiagonalDescend.value != false;
    }

    private void onChangeInPathPosition() {
        this.clearKeys();
        this.ticksOnCurrent = 0;
    }

    private void clearKeys() {
        this.behavior.itemics.getInputOverrideHandler().clearAllKeys();
    }

    private void cancel() {
        this.clearKeys();
        this.behavior.itemics.getInputOverrideHandler().getBlockBreakHelper().stopBreakingBlock();
        this.pathPosition = this.path.length() + 3;
        this.failed = true;
    }

    @Override
    public int getPosition() {
        return this.pathPosition;
    }

    public PathExecutor trySplice(PathExecutor next) {
        if (next == null) {
            return this.cutIfTooLong();
        }
        return SplicedPath.trySplice(this.path, next.path, false).map(path -> {
            if (!path.getDest().equals(next.getPath().getDest())) {
                throw new IllegalStateException();
            }
            PathExecutor ret = new PathExecutor(this.behavior, (IPath)path);
            ret.pathPosition = this.pathPosition;
            ret.currentMovementOriginalCostEstimate = this.currentMovementOriginalCostEstimate;
            ret.costEstimateIndex = this.costEstimateIndex;
            ret.ticksOnCurrent = this.ticksOnCurrent;
            return ret;
        }).orElseGet(this::cutIfTooLong);
    }

    private PathExecutor cutIfTooLong() {
        if (this.pathPosition > (Integer)Itemics.settings().maxPathHistoryLength.value) {
            int cutoffAmt = (Integer)Itemics.settings().pathHistoryCutoffAmount.value;
            CutoffPath newPath = new CutoffPath(this.path, cutoffAmt, this.path.length() - 1);
            if (!newPath.getDest().equals(this.path.getDest())) {
                throw new IllegalStateException();
            }
            this.logDebug("Discarding earliest segment movements, length cut from " + this.path.length() + " to " + newPath.length());
            PathExecutor ret = new PathExecutor(this.behavior, newPath);
            ret.pathPosition = this.pathPosition - cutoffAmt;
            ret.currentMovementOriginalCostEstimate = this.currentMovementOriginalCostEstimate;
            if (this.costEstimateIndex != null) {
                ret.costEstimateIndex = this.costEstimateIndex - cutoffAmt;
            }
            ret.ticksOnCurrent = this.ticksOnCurrent;
            return ret;
        }
        return this;
    }

    @Override
    public IPath getPath() {
        return this.path;
    }

    public boolean failed() {
        return this.failed;
    }

    public boolean finished() {
        return this.pathPosition >= this.path.length();
    }

    public Set<BlockPos> toBreak() {
        return Collections.unmodifiableSet(this.toBreak);
    }

    public Set<BlockPos> toPlace() {
        return Collections.unmodifiableSet(this.toPlace);
    }

    public Set<BlockPos> toWalkInto() {
        return Collections.unmodifiableSet(this.toWalkInto);
    }

    public boolean isSprinting() {
        return this.sprintNextTick;
    }
}
