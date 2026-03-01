package dimasik.managers.mods.voicechat.gui.group;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.CreateGroupScreen;
import dimasik.managers.mods.voicechat.gui.EnterPasswordScreen;
import dimasik.managers.mods.voicechat.gui.group.JoinGroupEntry;
import dimasik.managers.mods.voicechat.gui.group.JoinGroupList;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import dimasik.managers.mods.voicechat.net.ClientServerNetManager;
import dimasik.managers.mods.voicechat.net.JoinGroupPacket;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import java.awt.Color;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class JoinGroupScreen
extends ListScreenBase {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_join_group.png");
    protected static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.join_create_group.title");
    protected static final ITextComponent CREATE_GROUP = new TranslationTextComponent("message.voicechat.create_group_button");
    protected static final ITextComponent JOIN_CREATE_GROUP = new TranslationTextComponent("message.voicechat.join_create_group");
    protected static final ITextComponent NO_GROUPS = new TranslationTextComponent("message.voicechat.no_groups").setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())));
    protected static final int HEADER_SIZE = 16;
    protected static final int FOOTER_SIZE = 32;
    protected static final int UNIT_SIZE = 18;
    protected static final int CELL_HEIGHT = 36;
    protected JoinGroupList groupList;
    protected Button createGroup;
    protected int units;

    public JoinGroupScreen() {
        super(TITLE, 236, 0);
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft += 2;
        this.guiTop = 32;
        int minUnits = MathHelper.ceil(2.2222223f);
        this.units = Math.max(minUnits, (this.height - 16 - 32 - this.guiTop * 2) / 18);
        this.ySize = 16 + this.units * 18 + 32;
        if (this.groupList != null) {
            this.groupList.updateSize(this.width, this.units * 18, this.guiTop + 16);
        } else {
            this.groupList = new JoinGroupList(this, this.width, this.units * 18, this.guiTop + 16, 36);
        }
        this.addListener(this.groupList);
        this.createGroup = new Button(this.guiLeft + 7, this.guiTop + this.ySize - 20 - 7, this.xSize - 14, 20, CREATE_GROUP, button -> this.minecraft.displayGuiScreen(new CreateGroupScreen()));
        this.addButton(this.createGroup);
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(poseStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, 16);
        for (int i = 0; i < this.units; ++i) {
            this.blit(poseStack, this.guiLeft, this.guiTop + 16 + 18 * i, 0, 16, this.xSize, 18);
        }
        this.blit(poseStack, this.guiLeft, this.guiTop + 16 + 18 * this.units, 0, 34, this.xSize, 32);
        this.blit(poseStack, this.guiLeft + 10, this.guiTop + 16 + 6 - 2, this.xSize, 0, 12, 12);
    }

    @Override
    public void renderForeground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.fontRenderer.drawString(poseStack, JOIN_CREATE_GROUP.getString(), this.guiLeft + this.xSize / 2 - this.font.getStringWidth(JOIN_CREATE_GROUP.getString()) / 2, this.guiTop + 5, 0x404040);
        if (!this.groupList.isEmpty()) {
            this.groupList.render(poseStack, mouseX, mouseY, delta);
        } else {
            JoinGroupScreen.drawCenteredString(poseStack, this.font, NO_GROUPS, this.width / 2, this.guiTop + 16 + this.units * 18 / 2 - this.minecraft.fontRenderer.FONT_HEIGHT / 2, -1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (JoinGroupEntry entry : this.groupList.getChildren()) {
            if (!entry.isMouseOver(mouseX, mouseY)) continue;
            ClientGroup group = entry.getGroup().getGroup();
            this.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (group.hasPassword()) {
                this.minecraft.displayGuiScreen(new EnterPasswordScreen(group));
            } else {
                ClientServerNetManager.sendToServer(new JoinGroupPacket(group.getId(), null));
            }
            return true;
        }
        return false;
    }
}
