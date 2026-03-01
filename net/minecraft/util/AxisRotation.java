package net.minecraft.util;

import net.minecraft.util.Direction;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum AxisRotation {
    NONE{

        @Override
        public int getCoordinate(int x, int y, int z, Direction.Axis axis) {
            return axis.getCoordinate(x, y, z);
        }

        @Override
        public Direction.Axis rotate(Direction.Axis axisIn) {
            return axisIn;
        }

        @Override
        public AxisRotation reverse() {
            return this;
        }
    }
    ,
    FORWARD{

        @Override
        public int getCoordinate(int x, int y, int z, Direction.Axis axis) {
            return axis.getCoordinate(z, x, y);
        }

        @Override
        public Direction.Axis rotate(Direction.Axis axisIn) {
            return AXES[Math.floorMod(axisIn.ordinal() + 1, 3)];
        }

        @Override
        public AxisRotation reverse() {
            return BACKWARD;
        }
    }
    ,
    BACKWARD{

        @Override
        public int getCoordinate(int x, int y, int z, Direction.Axis axis) {
            return axis.getCoordinate(y, z, x);
        }

        @Override
        public Direction.Axis rotate(Direction.Axis axisIn) {
            return AXES[Math.floorMod(axisIn.ordinal() - 1, 3)];
        }

        @Override
        public AxisRotation reverse() {
            return FORWARD;
        }
    };

    public static final Direction.Axis[] AXES;
    public static final AxisRotation[] AXIS_ROTATIONS;

    public abstract int getCoordinate(int var1, int var2, int var3, Direction.Axis var4);

    public abstract Direction.Axis rotate(Direction.Axis var1);

    public abstract AxisRotation reverse();

    public static AxisRotation from(Direction.Axis axis1, Direction.Axis axis2) {
        return AXIS_ROTATIONS[Math.floorMod(axis2.ordinal() - axis1.ordinal(), 3)];
    }

    static {
        AXES = Direction.Axis.values();
        AXIS_ROTATIONS = AxisRotation.values();
    }
}
