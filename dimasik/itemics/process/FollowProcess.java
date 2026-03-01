package dimasik.itemics.process;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.pathing.goals.GoalComposite;
import dimasik.itemics.api.pathing.goals.GoalNear;
import dimasik.itemics.api.pathing.goals.GoalXZ;
import dimasik.itemics.api.process.IFollowProcess;
import dimasik.itemics.api.process.PathingCommand;
import dimasik.itemics.api.process.PathingCommandType;
import dimasik.itemics.utils.ItemicsProcessHelper;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public final class FollowProcess
extends ItemicsProcessHelper
implements IFollowProcess {
    private Predicate<Entity> filter;
    private List<Entity> cache;

    public FollowProcess(Itemics itemics) {
        super(itemics);
    }

    @Override
    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
        this.scanWorld();
        GoalComposite goal = new GoalComposite((Goal[])this.cache.stream().map(this::towards).toArray(Goal[]::new));
        return new PathingCommand(goal, PathingCommandType.REVALIDATE_GOAL_AND_PATH);
    }

    private Goal towards(Entity following) {
        BlockPos pos;
        if ((Double)Itemics.settings().followOffsetDistance.value == 0.0) {
            pos = following.getPosition();
        } else {
            GoalXZ g = GoalXZ.fromDirection(following.getPositionVec(), ((Float)Itemics.settings().followOffsetDirection.value).floatValue(), (Double)Itemics.settings().followOffsetDistance.value);
            pos = new BlockPos((double)g.getX(), following.getPositionVec().y, (double)g.getZ());
        }
        return new GoalNear(pos, (Integer)Itemics.settings().followRadius.value);
    }

    private boolean followable(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!entity.isAlive()) {
            return false;
        }
        if (entity.equals(this.ctx.player())) {
            return false;
        }
        return this.ctx.entitiesStream().anyMatch(entity::equals);
    }

    private void scanWorld() {
        this.cache = this.ctx.entitiesStream().filter(this::followable).filter(this.filter).distinct().collect(Collectors.toList());
    }

    @Override
    public boolean isActive() {
        if (this.filter == null) {
            return false;
        }
        this.scanWorld();
        return !this.cache.isEmpty();
    }

    @Override
    public void onLostControl() {
        this.filter = null;
        this.cache = null;
    }

    @Override
    public String displayName0() {
        return "Following " + String.valueOf(this.cache);
    }

    @Override
    public void follow(Predicate<Entity> filter) {
        this.filter = filter;
    }

    @Override
    public List<Entity> following() {
        return this.cache;
    }

    @Override
    public Predicate<Entity> currentFilter() {
        return this.filter;
    }
}
