package dimasik.events.main.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.client.renderer.ActiveRenderInfo;

public class EventGhostRender
implements Event {
    private final MatrixStack matrixStack;
    private final float partialTicks;
    private final ActiveRenderInfo renderInfo;

    @Generated
    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    @Generated
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Generated
    public ActiveRenderInfo getRenderInfo() {
        return this.renderInfo;
    }

    @Generated
    public EventGhostRender(MatrixStack matrixStack, float partialTicks, ActiveRenderInfo renderInfo) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
        this.renderInfo = renderInfo;
    }
}
