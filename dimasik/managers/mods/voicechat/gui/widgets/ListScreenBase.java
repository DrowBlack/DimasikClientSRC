package dimasik.managers.mods.voicechat.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.VoiceChatScreenBase;
import net.minecraft.util.text.ITextComponent;

public abstract class ListScreenBase
extends VoiceChatScreenBase {
    private Runnable postRender;

    public ListScreenBase(ITextComponent title, int xSize, int ySize) {
        super(title, xSize, ySize);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        super.render(poseStack, mouseX, mouseY, delta);
        if (this.postRender != null) {
            this.postRender.run();
            this.postRender = null;
        }
    }

    public void postRender(Runnable postRender) {
        this.postRender = postRender;
    }
}
