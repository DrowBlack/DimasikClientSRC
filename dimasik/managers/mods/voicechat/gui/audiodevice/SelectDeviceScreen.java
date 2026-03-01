package dimasik.managers.mods.voicechat.gui.audiodevice;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import dimasik.managers.mods.voicechat.gui.audiodevice.AudioDeviceList;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class SelectDeviceScreen
extends ListScreenBase {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_audio_devices.png");
    protected static final ITextComponent BACK = new TranslationTextComponent("message.voicechat.back");
    protected static final int HEADER_SIZE = 16;
    protected static final int FOOTER_SIZE = 32;
    protected static final int UNIT_SIZE = 18;
    @Nullable
    protected Screen parent;
    protected AudioDeviceList deviceList;
    protected Button back;
    protected int units;

    public SelectDeviceScreen(ITextComponent title, @Nullable Screen parent) {
        super(title, 236, 0);
        this.parent = parent;
    }

    public abstract List<String> getDevices();

    public abstract ResourceLocation getIcon();

    public abstract ITextComponent getEmptyListComponent();

    public abstract ConfigEntry<String> getConfigEntry();

    @Override
    protected void init() {
        super.init();
        this.guiLeft += 2;
        this.guiTop = 32;
        int minUnits = MathHelper.ceil(2.2222223f);
        this.units = Math.max(minUnits, (this.height - 16 - 32 - this.guiTop * 2) / 18);
        this.ySize = 16 + this.units * 18 + 32;
        if (this.deviceList != null) {
            this.deviceList.updateSize(this.width, this.units * 18, this.guiTop + 16);
        } else {
            this.deviceList = new AudioDeviceList(this.width, this.units * 18, this.guiTop + 16).setIcon(this.getIcon()).setConfigEntry(this.getConfigEntry());
        }
        this.addListener(this.deviceList);
        this.back = new Button(this.guiLeft + 7, this.guiTop + this.ySize - 20 - 7, this.xSize - 14, 20, BACK, button -> this.minecraft.displayGuiScreen(this.parent));
        this.addButton(this.back);
        this.deviceList.setAudioDevices(this.getDevices());
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        if (this.isIngame()) {
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.blit(poseStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, 16);
            for (int i = 0; i < this.units; ++i) {
                this.blit(poseStack, this.guiLeft, this.guiTop + 16 + 18 * i, 0, 16, this.xSize, 18);
            }
            this.blit(poseStack, this.guiLeft, this.guiTop + 16 + 18 * this.units, 0, 34, this.xSize, 32);
            this.blit(poseStack, this.guiLeft + 10, this.guiTop + 16 + 6 - 2, this.xSize, 0, 12, 12);
        }
    }

    @Override
    public void renderForeground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.fontRenderer.drawString(poseStack, this.title.getString(), this.width / 2 - this.font.getStringWidth(this.title.getString()) / 2, this.guiTop + 5, this.isIngame() ? 0x404040 : TextFormatting.WHITE.getColor());
        if (!this.deviceList.isEmpty()) {
            this.deviceList.render(poseStack, mouseX, mouseY, delta);
        } else {
            SelectDeviceScreen.drawCenteredString(poseStack, this.font, this.getEmptyListComponent(), this.width / 2, this.guiTop + 16 + this.units * 18 / 2 - this.minecraft.fontRenderer.FONT_HEIGHT / 2, -1);
        }
    }
}
