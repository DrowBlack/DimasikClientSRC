package dimasik.managers.module.option.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.option.api.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.vector.Vector2f;

public class DraggableOption
extends Option<Vector2f>
implements IFastAccess {
    private final ArrayList<Option<?>> options = new ArrayList();
    private float width;
    private float height;
    private boolean drag;
    private boolean click;
    private final Animation clickAnimation = new Animation();
    private float startX;
    private float startY;
    private float prevX;
    private float prevY;
    private final float gridSize = 100.0f;

    public DraggableOption(String settingName, Vector2f value, float width, float height) {
        super(settingName, value);
        this.width = width;
        this.height = height;
    }

    public DraggableOption visible(BooleanSupplier visible) {
        this.setVisible(visible);
        return this;
    }

    public DraggableOption description(String settingDescription) {
        this.settingDescription = settingDescription;
        return this;
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialsTicks) {
        if (this.drag) {
            this.setValue(new Vector2f((float)mouseX - this.prevX, (float)mouseY - this.prevY));
            this.setStartX(((Vector2f)this.getValue()).x + this.width);
            this.setStartY(((Vector2f)this.getValue()).y);
            this.applyBounds();
            if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 341)) {
                this.alignToGrid();
                this.drawGrid(matrixStack);
            }
        }
    }

    public void leftClick(int mouseX, int mouseY) {
        if (ScreenHelpers.isHovered(mouseX, mouseY, ((Vector2f)this.getValue()).x, ((Vector2f)this.getValue()).y, this.width, this.height)) {
            this.drag = true;
            this.prevX = (float)mouseX - ((Vector2f)this.getValue()).x;
            this.prevY = (float)mouseY - ((Vector2f)this.getValue()).y;
        }
    }

    public void rightClick(int mouseX, int mouseY) {
        if (ScreenHelpers.isHovered(mouseX, mouseY, ((Vector2f)this.getValue()).x, ((Vector2f)this.getValue()).y, this.width, this.height) && !this.getOptions().isEmpty()) {
            this.click = !this.click;
        }
    }

    public void release() {
        this.drag = false;
    }

    private void applyBounds() {
        float maxX = (float)mc.getMainWindow().getWidth() - this.width;
        float maxY = (float)mc.getMainWindow().getHeight() - this.height;
        Vector2f pos = (Vector2f)this.getValue();
        pos = new Vector2f(Math.max(0.0f, Math.min(pos.x, maxX)), Math.max(0.0f, Math.min(pos.y, maxY)));
        this.setValue(pos);
    }

    private void alignToGrid() {
        Vector2f pos = (Vector2f)this.getValue();
        float screenWidth = mc.getMainWindow().getWidth();
        float screenHeight = mc.getMainWindow().getHeight();
        float divisions = 8.0f;
        float dynamicGridSizeX = screenWidth / divisions;
        float dynamicGridSizeY = screenHeight / divisions;
        float[] snapX = new float[]{(float)Math.round(pos.x / dynamicGridSizeX) * dynamicGridSizeX, (float)Math.round((pos.x + this.width) / dynamicGridSizeX) * dynamicGridSizeX - this.width, (float)Math.round((pos.x + this.width / 2.0f) / dynamicGridSizeX) * dynamicGridSizeX - this.width / 2.0f};
        float[] snapY = new float[]{(float)Math.round(pos.y / dynamicGridSizeY) * dynamicGridSizeY, (float)Math.round((pos.y + this.height) / dynamicGridSizeY) * dynamicGridSizeY - this.height, (float)Math.round((pos.y + this.height / 2.0f) / dynamicGridSizeY) * dynamicGridSizeY - this.height / 2.0f};
        float closestX = snapX[0];
        float closestY = snapY[0];
        float minDistance = Float.MAX_VALUE;
        for (float sx : snapX) {
            for (float sy : snapY) {
                float distance = this.distance(pos.x, pos.y, sx, sy);
                if (!(distance < minDistance)) continue;
                minDistance = distance;
                closestX = sx;
                closestY = sy;
            }
        }
        closestX = Math.max(0.0f, Math.min(closestX, (float)mc.getMainWindow().getWidth() - this.width));
        closestY = Math.max(0.0f, Math.min(closestY, (float)mc.getMainWindow().getHeight() - this.height));
        this.setValue(new Vector2f(closestX, closestY));
        this.setStartX(closestX + this.width);
        this.setStartY(closestY);
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0));
    }

    private void drawGrid(MatrixStack matrixStack) {
        int gridColor;
        GLHelpers.INSTANCE.rescale(1.0);
        float screenWidth = mc.getMainWindow().getWidth() - 1;
        float screenHeight = mc.getMainWindow().getHeight() - 1;
        int defaultGridColor = 0x60FFFFFF;
        int snapGridColor = -256;
        float divisions = 8.0f;
        float dynamicGridSizeX = screenWidth / divisions;
        float dynamicGridSizeY = screenHeight / divisions;
        Vector2f pos = (Vector2f)this.getValue();
        float elementCenterX = pos.x + this.width / 2.0f;
        float elementCenterY = pos.y + this.height / 2.0f;
        float snapThreshold = 5.0f;
        for (float x = 0.0f; x <= screenWidth; x += dynamicGridSizeX) {
            gridColor = Math.abs(x - elementCenterX) < snapThreshold ? snapGridColor : defaultGridColor;
            VisualHelpers.drawRect(matrixStack, x, 0.0f, 1.0f, screenHeight, gridColor);
        }
        for (float y = 0.0f; y <= screenHeight; y += dynamicGridSizeY) {
            gridColor = Math.abs(y - elementCenterY) < snapThreshold ? snapGridColor : defaultGridColor;
            VisualHelpers.drawRect(matrixStack, 0.0f, y, screenWidth, 1.0f, gridColor);
        }
        GLHelpers.INSTANCE.rescaleMC();
    }

    public boolean checkOverlap(List<DraggableOption> otherOptions) {
        for (DraggableOption other : otherOptions) {
            if (other == this || !this.isOverlapping(other)) continue;
            return true;
        }
        return false;
    }

    private boolean isOverlapping(DraggableOption other) {
        Vector2f pos = (Vector2f)this.getValue();
        Vector2f otherPos = (Vector2f)other.getValue();
        return pos.x < otherPos.x + other.getWidth() && pos.x + this.width > otherPos.x && pos.y < otherPos.y + other.getHeight() && pos.y + this.height > otherPos.y;
    }

    public void settings(Option<?> ... options) {
        this.options.addAll(Arrays.asList(options));
    }

    @Generated
    public ArrayList<Option<?>> getOptions() {
        return this.options;
    }

    @Generated
    public float getWidth() {
        return this.width;
    }

    @Generated
    public void setWidth(float width) {
        this.width = width;
    }

    @Generated
    public float getHeight() {
        return this.height;
    }

    @Generated
    public void setHeight(float height) {
        this.height = height;
    }

    @Generated
    public void setDrag(boolean drag) {
        this.drag = drag;
    }

    @Generated
    public void setClick(boolean click) {
        this.click = click;
    }

    @Generated
    public boolean isClick() {
        return this.click;
    }

    @Generated
    public Animation getClickAnimation() {
        return this.clickAnimation;
    }

    @Generated
    public float getStartX() {
        return this.startX;
    }

    @Generated
    public float getStartY() {
        return this.startY;
    }

    @Generated
    public void setStartX(float startX) {
        this.startX = startX;
    }

    @Generated
    public void setStartY(float startY) {
        this.startY = startY;
    }
}
