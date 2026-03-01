package dimasik.ui.screen.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.ui.screen.UIScreen;
import lombok.Generated;

public abstract class Component
implements IFastAccess {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected final UIScreen parent;
    private boolean isSomeElementHovered = false;

    public Component(float x, float y, float width, float height, UIScreen parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }

    public abstract void render(MatrixStack var1, int var2, int var3, float var4);

    public abstract void exit();

    public abstract void tick();

    public abstract void keyPressed(int var1, int var2, int var3);

    public abstract void keyReleased(int var1, int var2, int var3);

    public abstract void charTyped(char var1, int var2);

    public abstract void mouseClicked(double var1, double var3, int var5);

    public abstract void mouseReleased(double var1, double var3, int var5);

    public abstract void mouseScrolled(double var1, double var3, double var5);

    public abstract void translate();

    public void renderWithCursorLogic(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.isSomeElementHovered = false;
        this.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void cursorLogic(int mouseX, int mouseY, float x, float y, float width, float height) {
        if (ScreenHelpers.isHovered(mouseX, mouseY, x, y, width, height)) {
            this.isSomeElementHovered = true;
        }
    }

    @Generated
    public float getX() {
        return this.x;
    }

    @Generated
    public float getY() {
        return this.y;
    }

    @Generated
    public float getWidth() {
        return this.width;
    }

    @Generated
    public float getHeight() {
        return this.height;
    }

    @Generated
    public UIScreen getParent() {
        return this.parent;
    }

    @Generated
    public boolean isSomeElementHovered() {
        return this.isSomeElementHovered;
    }

    @Generated
    public void setX(float x) {
        this.x = x;
    }

    @Generated
    public void setY(float y) {
        this.y = y;
    }

    @Generated
    public void setWidth(float width) {
        this.width = width;
    }

    @Generated
    public void setHeight(float height) {
        this.height = height;
    }
}
