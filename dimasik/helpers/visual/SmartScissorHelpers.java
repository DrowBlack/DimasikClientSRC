package dimasik.helpers.visual;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.helpers.interfaces.IFastAccess;
import lombok.Generated;

public final class SmartScissorHelpers
implements IFastAccess {
    public static void enable(int x, int y, int width, int height) {
        int windowHeight = mc.getMainWindow().getHeight();
        int adjustedY = windowHeight - (y + height);
        RenderSystem.enableScissor(x, adjustedY, width, height);
    }

    public static void enable(float x, float y, float width, float height) {
        int windowHeight = mc.getMainWindow().getHeight();
        int adjustedY = (int)((float)windowHeight - (y + height));
        RenderSystem.enableScissor((int)x, adjustedY, (int)width, (int)height);
    }

    public static void disable() {
        RenderSystem.disableScissor();
    }

    @Generated
    private SmartScissorHelpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
