package dimasik.itemics.api.event.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Matrix4f;

public final class RenderEvent {
    private final float partialTicks;
    private final Matrix4f projectionMatrix;
    private final MatrixStack modelViewStack;

    public RenderEvent(float partialTicks, MatrixStack modelViewStack, Matrix4f projectionMatrix) {
        this.partialTicks = partialTicks;
        this.modelViewStack = modelViewStack;
        this.projectionMatrix = projectionMatrix;
    }

    public final float getPartialTicks() {
        return this.partialTicks;
    }

    public MatrixStack getModelViewStack() {
        return this.modelViewStack;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
