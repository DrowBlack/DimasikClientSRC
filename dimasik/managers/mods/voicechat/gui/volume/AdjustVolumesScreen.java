package dimasik.managers.mods.voicechat.gui.volume;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumeList;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import java.awt.Color;
import java.util.Locale;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class AdjustVolumesScreen
extends ListScreenBase {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_volumes.png");
    protected static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.adjust_volume.title");
    protected static final ITextComponent SEARCH_HINT = new TranslationTextComponent("message.voicechat.search_hint").mergeStyle(TextFormatting.ITALIC).setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())));
    protected static final ITextComponent EMPTY_SEARCH = new TranslationTextComponent("message.voicechat.search_empty").mergeStyle(TextFormatting.GRAY);
    protected static final int HEADER_SIZE = 16;
    protected static final int FOOTER_SIZE = 8;
    protected static final int SEARCH_HEIGHT = 16;
    protected static final int UNIT_SIZE = 18;
    protected static final int CELL_HEIGHT = 36;
    protected AdjustVolumeList volumeList;
    protected TextFieldWidget searchBox;
    protected String lastSearch = "";
    protected int units;

    public AdjustVolumesScreen() {
        super(TITLE, 236, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft += 2;
        this.guiTop = 32;
        int minUnits = MathHelper.ceil(3.1111112f);
        this.units = Math.max(minUnits, (this.height - 16 - 8 - this.guiTop * 2 - 16) / 18);
        this.ySize = 16 + this.units * 18 + 8;
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        if (this.volumeList != null) {
            this.volumeList.updateSize(this.width, this.units * 18 - 16, this.guiTop + 16 + 16);
        } else {
            this.volumeList = new AdjustVolumeList(this.width, this.units * 18 - 16, this.guiTop + 16 + 16, 36, this);
        }
        String string = this.searchBox != null ? this.searchBox.getText() : "";
        this.searchBox = new TextFieldWidget(this.font, this.guiLeft + 28, this.guiTop + 16 + 6, 196, 16, SEARCH_HINT);
        this.searchBox.setMaxStringLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.searchBox.setText(string);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.addListener(this.searchBox);
        this.addListener(this.volumeList);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(poseStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, 16);
        for (int i = 0; i < this.units; ++i) {
            this.blit(poseStack, this.guiLeft, this.guiTop + 16 + 18 * i, 0, 16, this.xSize, 18);
        }
        this.blit(poseStack, this.guiLeft, this.guiTop + 16 + 18 * this.units, 0, 34, this.xSize, 8);
        this.blit(poseStack, this.guiLeft + 10, this.guiTop + 16 + 6 - 2, this.xSize, 0, 12, 12);
    }

    @Override
    public void renderForeground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.fontRenderer.drawString(poseStack, TITLE.getString(), this.width / 2 - this.font.getStringWidth(TITLE.getString()) / 2, this.guiTop + 5, 0x404040);
        if (!this.volumeList.isEmpty()) {
            this.volumeList.render(poseStack, mouseX, mouseY, delta);
        } else if (!this.searchBox.getText().isEmpty()) {
            AdjustVolumesScreen.drawCenteredString(poseStack, this.font, EMPTY_SEARCH, this.width / 2, this.guiTop + 16 + this.units * 18 / 2 - this.minecraft.fontRenderer.FONT_HEIGHT / 2, -1);
        }
        if (!this.searchBox.isFocused() && this.searchBox.getText().isEmpty()) {
            AdjustVolumesScreen.drawString(poseStack, this.font, SEARCH_HINT, this.searchBox.x, this.searchBox.y, -1);
        } else {
            this.searchBox.render(poseStack, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchBox.isFocused()) {
            this.searchBox.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button) || this.volumeList.mouseClicked(mouseX, mouseY, button);
    }

    private void checkSearchStringUpdate(String string) {
        if (!(string = string.toLowerCase(Locale.ROOT)).equals(this.lastSearch)) {
            this.volumeList.setFilter(string);
            this.lastSearch = string;
        }
    }
}
