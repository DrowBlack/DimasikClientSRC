package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public abstract class MoveToBlockGoal
extends Goal {
    protected final CreatureEntity creature;
    public final double movementSpeed;
    protected int runDelay;
    protected int timeoutCounter;
    private int maxStayTicks;
    protected BlockPos destinationBlock = BlockPos.ZERO;
    private boolean isAboveDestination;
    private final int searchLength;
    private final int field_203113_j;
    protected int field_203112_e;

    public MoveToBlockGoal(CreatureEntity creature, double speedIn, int length) {
        this(creature, speedIn, length, 1);
    }

    public MoveToBlockGoal(CreatureEntity creatureIn, double speed, int length, int p_i48796_5_) {
        this.creature = creatureIn;
        this.movementSpeed = speed;
        this.searchLength = length;
        this.field_203112_e = 0;
        this.field_203113_j = p_i48796_5_;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean shouldExecute() {
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        }
        this.runDelay = this.getRunDelay(this.creature);
        return this.searchForDestination();
    }

    protected int getRunDelay(CreatureEntity creatureIn) {
        return 200 + creatureIn.getRNG().nextInt(200);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.creature.world, this.destinationBlock);
    }

    @Override
    public void startExecuting() {
        this.func_220725_g();
        this.timeoutCounter = 0;
        this.maxStayTicks = this.creature.getRNG().nextInt(this.creature.getRNG().nextInt(1200) + 1200) + 1200;
    }

    protected void func_220725_g() {
        this.creature.getNavigator().tryMoveToXYZ((double)this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, (double)this.destinationBlock.getZ() + 0.5, this.movementSpeed);
    }

    public double getTargetDistanceSq() {
        return 1.0;
    }

    protected BlockPos func_241846_j() {
        return this.destinationBlock.up();
    }

    @Override
    public void tick() {
        BlockPos blockpos = this.func_241846_j();
        if (!blockpos.withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq())) {
            this.isAboveDestination = false;
            ++this.timeoutCounter;
            if (this.shouldMove()) {
                this.creature.getNavigator().tryMoveToXYZ((double)blockpos.getX() + 0.5, blockpos.getY(), (double)blockpos.getZ() + 0.5, this.movementSpeed);
            }
        } else {
            this.isAboveDestination = true;
            --this.timeoutCounter;
        }
    }

    public boolean shouldMove() {
        return this.timeoutCounter % 40 == 0;
    }

    protected boolean getIsAboveDestination() {
        return this.isAboveDestination;
    }

    protected boolean searchForDestination() {
        int i = this.searchLength;
        int j = this.field_203113_j;
        BlockPos blockpos = this.creature.getPosition();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int k = this.field_203112_e;
        while (k <= j) {
            for (int l = 0; l < i; ++l) {
                int i1 = 0;
                while (i1 <= l) {
                    int j1;
                    int n = j1 = i1 < l && i1 > -l ? l : 0;
                    while (j1 <= l) {
                        blockpos$mutable.setAndOffset(blockpos, i1, k - 1, j1);
                        if (this.creature.isWithinHomeDistanceFromPosition(blockpos$mutable) && this.shouldMoveTo(this.creature.world, blockpos$mutable)) {
                            this.destinationBlock = blockpos$mutable;
                            return true;
                        }
                        j1 = j1 > 0 ? -j1 : 1 - j1;
                    }
                    i1 = i1 > 0 ? -i1 : 1 - i1;
                }
            }
            k = k > 0 ? -k : 1 - k;
        }
        return false;
    }

    protected abstract boolean shouldMoveTo(IWorldReader var1, BlockPos var2);
}
