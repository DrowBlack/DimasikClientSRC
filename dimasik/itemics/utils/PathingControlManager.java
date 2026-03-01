package dimasik.itemics.utils;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.event.events.TickEvent;
import dimasik.itemics.api.event.listener.AbstractGameEventListener;
import dimasik.itemics.api.pathing.calc.IPathingControlManager;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.process.IItemicsProcess;
import dimasik.itemics.api.process.PathingCommand;
import dimasik.itemics.api.process.PathingCommandType;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.behavior.PathingBehavior;
import dimasik.itemics.pathing.path.PathExecutor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PathingControlManager
implements IPathingControlManager {
    private final Itemics itemics;
    private final HashSet<IItemicsProcess> processes;
    private final List<IItemicsProcess> active;
    private IItemicsProcess inControlLastTick;
    private IItemicsProcess inControlThisTick;
    private PathingCommand command;

    public PathingControlManager(Itemics itemics) {
        this.itemics = itemics;
        this.processes = new HashSet();
        this.active = new ArrayList<IItemicsProcess>();
        itemics.getGameEventHandler().registerEventListener(new AbstractGameEventListener(){

            @Override
            public void onTick(TickEvent event) {
                if (event.getType() == TickEvent.Type.IN) {
                    PathingControlManager.this.postTick();
                }
            }
        });
    }

    @Override
    public void registerProcess(IItemicsProcess process) {
        process.onLostControl();
        this.processes.add(process);
    }

    public void cancelEverything() {
        this.inControlLastTick = null;
        this.inControlThisTick = null;
        this.command = null;
        this.active.clear();
        for (IItemicsProcess proc : this.processes) {
            proc.onLostControl();
            if (!proc.isActive() || proc.isTemporary()) continue;
            throw new IllegalStateException(proc.displayName());
        }
    }

    @Override
    public Optional<IItemicsProcess> mostRecentInControl() {
        return Optional.ofNullable(this.inControlThisTick);
    }

    @Override
    public Optional<PathingCommand> mostRecentCommand() {
        return Optional.ofNullable(this.command);
    }

    public void preTick() {
        this.inControlLastTick = this.inControlThisTick;
        this.inControlThisTick = null;
        PathingBehavior p = this.itemics.getPathingBehavior();
        this.command = this.executeProcesses();
        if (this.command == null) {
            p.cancelSegmentIfSafe();
            p.secretInternalSetGoal(null);
            return;
        }
        if (!Objects.equals(this.inControlThisTick, this.inControlLastTick) && this.command.commandType != PathingCommandType.REQUEST_PAUSE && this.inControlLastTick != null && !this.inControlLastTick.isTemporary()) {
            p.cancelSegmentIfSafe();
        }
        switch (this.command.commandType) {
            case REQUEST_PAUSE: {
                p.requestPause();
                break;
            }
            case CANCEL_AND_SET_GOAL: {
                p.secretInternalSetGoal(this.command.goal);
                p.cancelSegmentIfSafe();
                break;
            }
            case FORCE_REVALIDATE_GOAL_AND_PATH: {
                if (p.isPathing() || p.getInProgress().isPresent()) break;
                p.secretInternalSetGoalAndPath(this.command);
                break;
            }
            case REVALIDATE_GOAL_AND_PATH: {
                if (p.isPathing() || p.getInProgress().isPresent()) break;
                p.secretInternalSetGoalAndPath(this.command);
                break;
            }
            case SET_GOAL_AND_PATH: {
                if (this.command.goal == null) break;
                this.itemics.getPathingBehavior().secretInternalSetGoalAndPath(this.command);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    private void postTick() {
        if (this.command == null) {
            return;
        }
        PathingBehavior p = this.itemics.getPathingBehavior();
        switch (this.command.commandType) {
            case FORCE_REVALIDATE_GOAL_AND_PATH: {
                if (this.command.goal == null || this.forceRevalidate(this.command.goal) || this.revalidateGoal(this.command.goal)) {
                    p.softCancelIfSafe();
                }
                p.secretInternalSetGoalAndPath(this.command);
                break;
            }
            case REVALIDATE_GOAL_AND_PATH: {
                if (((Boolean)Itemics.settings().cancelOnGoalInvalidation.value).booleanValue() && (this.command.goal == null || this.revalidateGoal(this.command.goal))) {
                    p.softCancelIfSafe();
                }
                p.secretInternalSetGoalAndPath(this.command);
                break;
            }
        }
    }

    public boolean forceRevalidate(Goal newGoal) {
        PathExecutor current = this.itemics.getPathingBehavior().getCurrent();
        if (current != null) {
            if (newGoal.isInGoal(current.getPath().getDest())) {
                return false;
            }
            return !newGoal.toString().equals(current.getPath().getGoal().toString());
        }
        return false;
    }

    public boolean revalidateGoal(Goal newGoal) {
        BetterBlockPos end;
        Goal intended;
        PathExecutor current = this.itemics.getPathingBehavior().getCurrent();
        return current != null && (intended = current.getPath().getGoal()).isInGoal(end = current.getPath().getDest()) && !newGoal.isInGoal(end);
    }

    public PathingCommand executeProcesses() {
        for (IItemicsProcess process : this.processes) {
            if (process.isActive()) {
                if (this.active.contains(process)) continue;
                this.active.add(0, process);
                continue;
            }
            this.active.remove(process);
        }
        this.active.sort(Comparator.comparingDouble(IItemicsProcess::priority).reversed());
        Iterator<IItemicsProcess> iterator = this.active.iterator();
        while (iterator.hasNext()) {
            IItemicsProcess proc;
            PathingCommand exec = proc.onTick(Objects.equals(proc = iterator.next(), this.inControlLastTick) && this.itemics.getPathingBehavior().calcFailedLastTick(), this.itemics.getPathingBehavior().isSafeToCancel());
            if (exec == null) {
                if (!proc.isActive()) continue;
                throw new IllegalStateException(proc.displayName() + " actively returned null PathingCommand");
            }
            if (exec.commandType == PathingCommandType.DEFER) continue;
            this.inControlThisTick = proc;
            if (!proc.isTemporary()) {
                iterator.forEachRemaining(IItemicsProcess::onLostControl);
            }
            return exec;
        }
        return null;
    }
}
