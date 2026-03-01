package dimasik.managers.mods.voicechat.gui.audiodevice;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenEntryBase;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.ResourceLocation;

public class AudioDeviceEntry
extends ListScreenEntryBase<AudioDeviceEntry> {
    protected static final ResourceLocation SELECTED = new ResourceLocation("main/textures/images/icons/device_selected.png");
    protected static final int PADDING = 4;
    protected static final int BG_FILL = new Color(74, 74, 74).getRGB();
    protected static final int BG_FILL_HOVERED = new Color(90, 90, 90).getRGB();
    protected static final int BG_FILL_SELECTED = new Color(40, 40, 40).getRGB();
    protected static final int DEVICE_NAME_COLOR = new Color(255, 255, 255).getRGB();
    protected final Minecraft minecraft;
    protected final String device;
    protected final String visibleDeviceName;
    @Nullable
    protected final ResourceLocation icon;
    protected final Supplier<Boolean> isSelected;

    public AudioDeviceEntry(String device, String name, @Nullable ResourceLocation icon, Supplier<Boolean> isSelected) {
        this.device = device;
        this.icon = icon;
        this.isSelected = isSelected;
        this.visibleDeviceName = name;
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(MatrixStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
        boolean selected = this.isSelected.get();
        if (selected) {
            AbstractGui.fill(poseStack, left, top, left + width, top + height, BG_FILL_SELECTED);
        } else if (hovered) {
            AbstractGui.fill(poseStack, left, top, left + width, top + height, BG_FILL_HOVERED);
        } else {
            AbstractGui.fill(poseStack, left, top, left + width, top + height, BG_FILL);
        }
        if (this.icon != null) {
            this.minecraft.getTextureManager().bindTexture(this.icon);
            AbstractGui.blit(poseStack, left + 4, top + height / 2 - 8, 16.0f, 16.0f, 16, 16, 16, 16);
        }
        if (selected) {
            this.minecraft.getTextureManager().bindTexture(SELECTED);
            AbstractGui.blit(poseStack, left + 4, top + height / 2 - 8, 16.0f, 16.0f, 16, 16, 16, 16);
        }
        float deviceWidth = this.minecraft.fontRenderer.getStringWidth(this.visibleDeviceName);
        float space = width - 4 - 16 - 4 - 4;
        float scale = Math.min(space / deviceWidth, 1.0f);
        poseStack.push();
        double d = left + 4 + 16 + 4;
        float f = top + height / 2;
        Objects.requireNonNull(this.minecraft.fontRenderer);
        poseStack.translate(d, f - 9.0f * scale / 2.0f, 0.0);
        poseStack.scale(scale, scale, 1.0f);
        this.minecraft.fontRenderer.drawString(poseStack, this.visibleDeviceName, 0.0f, 0.0f, DEVICE_NAME_COLOR);
        poseStack.pop();
    }

    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        return List.of();
    }

    @Generated
    public String getDevice() {
        return this.device;
    }
}
