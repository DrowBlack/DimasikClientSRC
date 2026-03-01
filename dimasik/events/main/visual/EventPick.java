package dimasik.events.main.visual;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventPick
extends EventCancellable
implements Event {
    private final boolean right;
    private final float progress;
    private final MatrixStack matrixStack;

    @Generated
    public boolean isRight() {
        return this.right;
    }

    @Generated
    public float getProgress() {
        return this.progress;
    }

    @Generated
    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    @Generated
    public EventPick(boolean right, float progress, MatrixStack matrixStack) {
        this.right = right;
        this.progress = progress;
        this.matrixStack = matrixStack;
    }
}
