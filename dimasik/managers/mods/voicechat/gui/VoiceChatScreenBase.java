package dimasik.managers.mods.voicechat.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public abstract class VoiceChatScreenBase
extends Screen {
    public static final int FONT_COLOR = 0x404040;
    protected List<HoverArea> hoverAreas;
    protected int guiLeft;
    protected int guiTop;
    protected int xSize;
    protected int ySize;

    protected VoiceChatScreenBase(ITextComponent title, int xSize, int ySize) {
        super(title);
        this.xSize = xSize;
        this.ySize = ySize;
        this.hoverAreas = new ArrayList<HoverArea>();
    }

    @Override
    protected void init() {
        this.buttons.clear();
        this.children.clear();
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(poseStack);
        this.renderBackground(poseStack, mouseX, mouseY, delta);
        super.render(poseStack, mouseX, mouseY, delta);
        this.renderForeground(poseStack, mouseX, mouseY, delta);
    }

    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
    }

    public void renderForeground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
    }

    public int getGuiLeft() {
        return this.guiLeft;
    }

    public int getGuiTop() {
        return this.guiTop;
    }

    protected boolean isIngame() {
        return this.minecraft.world != null;
    }

    protected int getFontColor() {
        return this.isIngame() ? 0x404040 : TextFormatting.WHITE.getColor();
    }

    public void drawHoverAreas(MatrixStack matrixStack, int mouseX, int mouseY) {
        for (HoverArea hoverArea : this.hoverAreas) {
            if (hoverArea.tooltip == null || !hoverArea.isHovered(this.guiLeft, this.guiTop, mouseX, mouseY)) continue;
            this.renderTooltip(matrixStack, hoverArea.tooltip.get(), mouseX - this.guiLeft, mouseY - this.guiTop);
        }
    }

    public static class HoverArea {
        private final int posX;
        private final int posY;
        private final int width;
        private final int height;
        @Nullable
        private final Supplier<List<IReorderingProcessor>> tooltip;

        public HoverArea(int posX, int posY, int width, int height) {
            this(posX, posY, width, height, null);
        }

        public HoverArea(int posX, int posY, int width, int height, Supplier<List<IReorderingProcessor>> tooltip) {
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
            this.tooltip = tooltip;
        }

        public int getPosX() {
            return this.posX;
        }

        public int getPosY() {
            return this.posY;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        @Nullable
        public Supplier<List<IReorderingProcessor>> getTooltip() {
            return this.tooltip;
        }

        public boolean isHovered(int guiLeft, int guiTop, int mouseX, int mouseY) {
            return mouseX >= guiLeft + this.posX && mouseX < guiLeft + this.posX + this.width && mouseY >= guiTop + this.posY && mouseY < guiTop + this.posY + this.height;
        }
    }
}
