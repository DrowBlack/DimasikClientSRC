package dimasik.itemics.behavior;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.behavior.ILookBehavior;
import dimasik.itemics.api.event.events.PlayerUpdateEvent;
import dimasik.itemics.api.event.events.RotationMoveEvent;
import dimasik.itemics.api.utils.Rotation;
import dimasik.itemics.behavior.Behavior;

public final class LookBehavior
extends Behavior
implements ILookBehavior {
    private Rotation target;
    private boolean force;
    private float lastYaw;

    public LookBehavior(Itemics itemics) {
        super(itemics);
    }

    @Override
    public void updateTarget(Rotation target, boolean force) {
        this.target = target;
        if (!force) {
            double rand = Math.random() - 0.5;
            if (Math.abs(rand) < 0.1) {
                rand *= 4.0;
            }
            this.target = new Rotation(this.target.getYaw() + (float)(rand * (Double)Itemics.settings().randomLooking113.value), this.target.getPitch());
        }
        this.force = force || (Boolean)Itemics.settings().freeLook.value == false;
    }

    @Override
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (this.target == null) {
            return;
        }
        boolean silent = (Boolean)Itemics.settings().antiCheatCompatibility.value != false && !this.force;
        switch (event.getState()) {
            case PRE: {
                if (this.force) {
                    float desiredPitch;
                    this.ctx.player().packetYaw = this.target.getYaw();
                    float oldPitch = this.ctx.player().packetPitch;
                    this.ctx.player().packetPitch = desiredPitch = this.target.getPitch();
                    this.ctx.player().packetYaw = (float)((double)this.ctx.player().packetYaw + (Math.random() - 0.5) * (Double)Itemics.settings().randomLooking.value);
                    this.ctx.player().packetPitch = (float)((double)this.ctx.player().packetPitch + (Math.random() - 0.5) * (Double)Itemics.settings().randomLooking.value);
                    if (desiredPitch == oldPitch && !((Boolean)Itemics.settings().freeLook.value).booleanValue()) {
                        this.nudgeToLevel();
                    }
                    this.ctx.player().rotationYawHead = this.ctx.player().packetYaw;
                    this.ctx.player().rotationPitchHead = this.ctx.player().packetPitch;
                    this.ctx.player().renderYawOffset = this.ctx.player().packetYaw;
                    this.target = null;
                }
                if (!silent) break;
                this.lastYaw = this.ctx.player().packetYaw;
                this.ctx.player().packetYaw = this.target.getYaw();
                break;
            }
            case POST: {
                if (!silent) break;
                this.ctx.player().packetYaw = this.lastYaw;
                this.target = null;
                break;
            }
        }
    }

    public void pig() {
        if (this.target != null) {
            this.ctx.player().packetYaw = this.target.getYaw();
        }
    }

    @Override
    public void onPlayerRotationMove(RotationMoveEvent event) {
        if (this.target != null) {
            this.ctx.player().rotationYawHead = this.target.getYaw();
            this.ctx.player().renderYawOffset = this.target.getYaw();
            this.ctx.player().packetYaw = this.target.getYaw();
            event.setYaw(this.target.getYaw());
            if (!((Boolean)Itemics.settings().antiCheatCompatibility.value).booleanValue() && event.getType() == RotationMoveEvent.Type.MOTION_UPDATE && !this.force) {
                this.target = null;
            }
        }
    }

    private void nudgeToLevel() {
        if (this.ctx.player().packetPitch < -20.0f) {
            this.ctx.player().packetPitch += 1.0f;
        } else if (this.ctx.player().packetPitch > 10.0f) {
            this.ctx.player().packetPitch -= 1.0f;
        }
    }
}
