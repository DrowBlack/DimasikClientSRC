package dimasik.events.main.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.client.MainWindow;

public class EventRender2D
implements Event {
    private final MainWindow mainWindow;
    private final MatrixStack matrixStack;
    private final float partialTicks;

    public EventRender2D(MainWindow mainWindow, MatrixStack matrixStack, float partialTicks) {
        this.mainWindow = mainWindow;
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }

    @Generated
    public MainWindow getMainWindow() {
        return this.mainWindow;
    }

    @Generated
    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    @Generated
    public float getPartialTicks() {
        return this.partialTicks;
    }

    public static class Hunger
    extends EventRender2D {
        public Hunger(MainWindow mainWindow, MatrixStack matrixStack, float partialTicks) {
            super(mainWindow, matrixStack, partialTicks);
        }
    }

    public static class Post
    extends EventRender2D {
        public Post(MainWindow mainWindow, MatrixStack matrixStack, float partialTicks) {
            super(mainWindow, matrixStack, partialTicks);
        }
    }

    public static class Pre
    extends EventRender2D {
        public Pre(MainWindow mainWindow, MatrixStack matrixStack, float partialTicks) {
            super(mainWindow, matrixStack, partialTicks);
        }
    }
}
