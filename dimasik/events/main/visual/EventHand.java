package dimasik.events.main.visual;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.events.api.main.Event;
import lombok.Generated;

public class EventHand
implements Event {
    private MatrixStack matrixStack;
    private float swingProgress;
    private float speed;
    private boolean update;
    private float equipProgress;
    private float leftX;
    private float leftY;
    private float leftZ;
    private float rightX;
    private float rightY;
    private float rightZ;

    private EventHand(MatrixStack matrixStack, float swingProgress) {
        this.matrixStack = matrixStack;
        this.swingProgress = swingProgress;
    }

    private EventHand(float speed) {
        this.speed = speed;
    }

    private EventHand(boolean update) {
        this.update = update;
    }

    private EventHand(float leftX, float leftY, float leftZ, float rightY, float rightX, float rightZ) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.leftZ = leftZ;
        this.rightX = rightX;
        this.rightY = rightY;
        this.rightZ = rightZ;
    }

    @Generated
    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    @Generated
    public float getSwingProgress() {
        return this.swingProgress;
    }

    @Generated
    public float getSpeed() {
        return this.speed;
    }

    @Generated
    public boolean isUpdate() {
        return this.update;
    }

    @Generated
    public float getEquipProgress() {
        return this.equipProgress;
    }

    @Generated
    public float getLeftX() {
        return this.leftX;
    }

    @Generated
    public float getLeftY() {
        return this.leftY;
    }

    @Generated
    public float getLeftZ() {
        return this.leftZ;
    }

    @Generated
    public float getRightX() {
        return this.rightX;
    }

    @Generated
    public float getRightY() {
        return this.rightY;
    }

    @Generated
    public float getRightZ() {
        return this.rightZ;
    }

    @Generated
    public void setMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    @Generated
    public void setSwingProgress(float swingProgress) {
        this.swingProgress = swingProgress;
    }

    @Generated
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Generated
    public void setUpdate(boolean update) {
        this.update = update;
    }

    @Generated
    public void setEquipProgress(float equipProgress) {
        this.equipProgress = equipProgress;
    }

    @Generated
    public void setLeftX(float leftX) {
        this.leftX = leftX;
    }

    @Generated
    public void setLeftY(float leftY) {
        this.leftY = leftY;
    }

    @Generated
    public void setLeftZ(float leftZ) {
        this.leftZ = leftZ;
    }

    @Generated
    public void setRightX(float rightX) {
        this.rightX = rightX;
    }

    @Generated
    public void setRightY(float rightY) {
        this.rightY = rightY;
    }

    @Generated
    public void setRightZ(float rightZ) {
        this.rightZ = rightZ;
    }

    public static class Update
    extends EventHand {
        public Update(boolean update) {
            super(update);
        }
    }

    public static class Speed
    extends EventHand {
        public Speed(float speed) {
            super(speed);
        }
    }

    public static class Animation
    extends EventHand {
        public Animation(MatrixStack matrixStack, float swingProgress) {
            super(matrixStack, swingProgress);
        }
    }

    public static class Position
    extends EventHand {
        public Position(float leftX, float leftY, float leftZ, float rightY, float rightX, float rightZ) {
            super(leftX, leftY, leftZ, rightY, rightX, rightZ);
        }
    }
}
