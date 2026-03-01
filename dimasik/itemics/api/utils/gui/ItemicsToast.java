package dimasik.itemics.api.utils.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import dimasik.itemics.api.ItemicsAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ItemicsToast
implements IToast {
    private String title;
    private String subtitle;
    private long firstDrawTime;
    private boolean newDisplay;
    private long totalShowTime;

    public ItemicsToast(ITextComponent titleComponent, ITextComponent subtitleComponent, long totalShowTime) {
        this.title = titleComponent.getString();
        this.subtitle = subtitleComponent == null ? null : subtitleComponent.getString();
        this.totalShowTime = totalShowTime;
    }

    @Override
    public IToast.Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long delta) {
        if (this.newDisplay) {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }
        toastGui.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/toasts.png"));
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 255.0f);
        toastGui.blit(matrixStack, 0, 0, 0, 32, 160, 32);
        if (this.subtitle == null) {
            toastGui.getMinecraft().fontRenderer.drawString(matrixStack, this.title, 18.0f, 12.0f, -11534256);
        } else {
            toastGui.getMinecraft().fontRenderer.drawString(matrixStack, this.title, 18.0f, 7.0f, -11534256);
            toastGui.getMinecraft().fontRenderer.drawString(matrixStack, this.subtitle, 18.0f, 18.0f, -16777216);
        }
        return delta - this.firstDrawTime < this.totalShowTime ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    public void setDisplayedText(ITextComponent titleComponent, ITextComponent subtitleComponent) {
        this.title = titleComponent.getString();
        this.subtitle = subtitleComponent == null ? null : subtitleComponent.getString();
        this.newDisplay = true;
    }

    public static void addOrUpdate(ToastGui toast, ITextComponent title, ITextComponent subtitle, long totalShowTime) {
        ItemicsToast itemicstoast = toast.getToast(ItemicsToast.class, new Object());
        if (itemicstoast == null) {
            toast.add(new ItemicsToast(title, subtitle, totalShowTime));
        } else {
            itemicstoast.setDisplayedText(title, subtitle);
        }
    }

    public static void addOrUpdate(ITextComponent title, ITextComponent subtitle) {
        ItemicsToast.addOrUpdate(Minecraft.getInstance().getToastGui(), title, subtitle, (Long)ItemicsAPI.getSettings().toastTimer.value);
    }
}
