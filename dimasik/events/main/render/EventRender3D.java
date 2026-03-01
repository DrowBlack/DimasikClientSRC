package dimasik.events.main.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.events.api.main.Event;
import lombok.Generated;

public class EventRender3D
implements Event {
    private final MatrixStack matrixStack;
    private final float partialTicks;

    @Generated
    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    @Generated
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Generated
    public EventRender3D(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }

    public static class Post
    extends EventRender3D {
        public Post(MatrixStack matrixStack, float partialTicks) {
            super(matrixStack, partialTicks);
        }
    }

    public static class PostAll
    extends EventRender3D {
        public PostAll(MatrixStack matrixStack, float partialTicks) {
            super(matrixStack, partialTicks);
        }
    }

    public static class PostEntity
    extends EventRender3D {
        public PostEntity(MatrixStack matrixStack, float partialTicks) {
            super(matrixStack, partialTicks);
        }
    }

    public static class PreEntity
    extends EventRender3D {
        public PreEntity(MatrixStack matrixStack, float partialTicks) {
            super(matrixStack, partialTicks);
        }
    }

    public static class PreHand
    extends EventRender3D {
        public PreHand(MatrixStack matrixStack, float partialTicks) {
            super(matrixStack, partialTicks);
        }
    }
}
