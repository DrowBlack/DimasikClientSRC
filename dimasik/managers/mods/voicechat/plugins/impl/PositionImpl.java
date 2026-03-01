package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.Position;
import java.util.Objects;
import net.minecraft.util.math.vector.Vector3d;

public class PositionImpl
implements Position {
    private final Vector3d position;

    public PositionImpl(Vector3d position) {
        this.position = position;
    }

    public PositionImpl(double x, double y, double z) {
        this.position = new Vector3d(x, y, z);
    }

    @Override
    public double getX() {
        return this.position.x;
    }

    @Override
    public double getY() {
        return this.position.y;
    }

    @Override
    public double getZ() {
        return this.position.z;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        PositionImpl position1 = (PositionImpl)object;
        return Objects.equals(this.position, position1.position);
    }

    public int hashCode() {
        return this.position != null ? this.position.hashCode() : 0;
    }
}
