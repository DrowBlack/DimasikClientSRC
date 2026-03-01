package dimasik.itemics.api.pathing.goals;

import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.SettingsUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class GoalXZ
implements Goal {
    private static final double SQRT_2 = Math.sqrt(2.0);
    private final int x;
    private final int z;

    public GoalXZ(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public GoalXZ(BetterBlockPos pos) {
        this.x = pos.x;
        this.z = pos.z;
    }

    @Override
    public boolean isInGoal(int x, int y, int z) {
        return x == this.x && z == this.z;
    }

    @Override
    public double heuristic(int x, int y, int z) {
        int xDiff = x - this.x;
        int zDiff = z - this.z;
        return GoalXZ.calculate(xDiff, zDiff);
    }

    public String toString() {
        return String.format("GoalXZ{x=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.z));
    }

    public static double calculate(double xDiff, double zDiff) {
        double straight;
        double z;
        double x = Math.abs(xDiff);
        if (x < (z = Math.abs(zDiff))) {
            straight = z - x;
            diagonal = x;
        } else {
            straight = x - z;
            diagonal = z;
        }
        return ((diagonal *= SQRT_2) + straight) * (Double)ItemicsAPI.getSettings().costHeuristic.value;
    }

    public static GoalXZ fromDirection(Vector3d origin, float yaw, double distance) {
        float theta = (float)Math.toRadians(yaw);
        double x = origin.x - (double)MathHelper.sin(theta) * distance;
        double z = origin.z + (double)MathHelper.cos(theta) * distance;
        return new GoalXZ(MathHelper.floor(x), MathHelper.floor(z));
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}
