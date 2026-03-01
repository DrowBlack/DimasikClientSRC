package dimasik.events.main.movement;

import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class MovingEvent
implements Event {
    private Vector3d from;
    private Vector3d to;
    private Vector3d motion;
    private boolean toGround;
    private AxisAlignedBB aabbFrom;
    private boolean ignoreHorizontal;
    private boolean ignoreVertical;
    private boolean collidedHorizontal;
    private boolean collidedVertical;

    public MovingEvent(Vector3d from, Vector3d to, Vector3d motion, boolean toGround, boolean isCollidedHorizontal, boolean isCollidedVertical, AxisAlignedBB aabbFrom) {
        this.from = from;
        this.to = to;
        this.motion = motion;
        this.toGround = toGround;
        this.collidedHorizontal = isCollidedHorizontal;
        this.collidedVertical = isCollidedVertical;
        this.aabbFrom = aabbFrom;
    }

    @Generated
    public Vector3d getFrom() {
        return this.from;
    }

    @Generated
    public Vector3d getTo() {
        return this.to;
    }

    @Generated
    public Vector3d getMotion() {
        return this.motion;
    }

    @Generated
    public boolean isToGround() {
        return this.toGround;
    }

    @Generated
    public AxisAlignedBB getAabbFrom() {
        return this.aabbFrom;
    }

    @Generated
    public boolean isIgnoreHorizontal() {
        return this.ignoreHorizontal;
    }

    @Generated
    public boolean isIgnoreVertical() {
        return this.ignoreVertical;
    }

    @Generated
    public boolean isCollidedHorizontal() {
        return this.collidedHorizontal;
    }

    @Generated
    public boolean isCollidedVertical() {
        return this.collidedVertical;
    }

    @Generated
    public void setFrom(Vector3d from) {
        this.from = from;
    }

    @Generated
    public void setTo(Vector3d to) {
        this.to = to;
    }

    @Generated
    public void setMotion(Vector3d motion) {
        this.motion = motion;
    }

    @Generated
    public void setToGround(boolean toGround) {
        this.toGround = toGround;
    }

    @Generated
    public void setAabbFrom(AxisAlignedBB aabbFrom) {
        this.aabbFrom = aabbFrom;
    }

    @Generated
    public void setIgnoreHorizontal(boolean ignoreHorizontal) {
        this.ignoreHorizontal = ignoreHorizontal;
    }

    @Generated
    public void setIgnoreVertical(boolean ignoreVertical) {
        this.ignoreVertical = ignoreVertical;
    }

    @Generated
    public void setCollidedHorizontal(boolean collidedHorizontal) {
        this.collidedHorizontal = collidedHorizontal;
    }

    @Generated
    public void setCollidedVertical(boolean collidedVertical) {
        this.collidedVertical = collidedVertical;
    }
}
