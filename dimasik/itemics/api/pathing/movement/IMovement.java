package dimasik.itemics.api.pathing.movement;

import dimasik.itemics.api.pathing.movement.MovementStatus;
import dimasik.itemics.api.utils.BetterBlockPos;
import net.minecraft.util.math.BlockPos;

public interface IMovement {
    public double getCost();

    public MovementStatus update();

    public void reset();

    public void resetBlockCache();

    public boolean safeToCancel();

    public boolean calculatedWhileLoaded();

    public BetterBlockPos getSrc();

    public BetterBlockPos getDest();

    public BlockPos getDirection();
}
