package dimasik.managers.mods.voicechat.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenEntryBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;

public abstract class ListScreenListBase<T extends ListScreenEntryBase<T>>
extends AbstractOptionList<T> {
    public ListScreenListBase(int width, int height, int top, int size) {
        super(Minecraft.getInstance(), width, height, top, top + height, size);
    }

    public void updateSize(int width, int height, int top) {
        this.updateSize(width, height, top, top + height);
    }

    @Override
    public void render(MatrixStack poseStack, int x, int y, float partialTicks) {
        double scale = this.minecraft.getMainWindow().getGuiScaleFactor();
        int scaledHeight = this.minecraft.getMainWindow().getScaledHeight();
        RenderSystem.enableScissor(0, (int)((double)(scaledHeight - this.y1) * scale), 0x3FFFFFFF, (int)((double)this.height * scale));
        super.render(poseStack, x, y, partialTicks);
        RenderSystem.disableScissor();
    }
}
