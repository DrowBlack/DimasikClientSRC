package dimasik.managers.mods.voicechat.gui.group;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.gui.GroupType;
import dimasik.managers.mods.voicechat.gui.group.GroupList;
import dimasik.managers.mods.voicechat.gui.group.JoinGroupScreen;
import dimasik.managers.mods.voicechat.gui.tooltips.DisableTooltipSupplier;
import dimasik.managers.mods.voicechat.gui.tooltips.HideGroupHudTooltipSupplier;
import dimasik.managers.mods.voicechat.gui.tooltips.MuteTooltipSupplier;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import dimasik.managers.mods.voicechat.gui.widgets.ToggleImageButton;
import dimasik.managers.mods.voicechat.net.ClientServerNetManager;
import dimasik.managers.mods.voicechat.net.LeaveGroupPacket;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import java.util.Collections;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GroupScreen
extends ListScreenBase {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_group.png");
    protected static final ResourceLocation LEAVE = new ResourceLocation("main/textures/images/icons/leave.png");
    protected static final ResourceLocation MICROPHONE = new ResourceLocation("main/textures/images/icons/microphone_button.png");
    protected static final ResourceLocation SPEAKER = new ResourceLocation("main/textures/images/icons/speaker_button.png");
    protected static final ResourceLocation GROUP_HUD = new ResourceLocation("main/textures/images/icons/group_hud_button.png");
    protected static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.group.title");
    protected static final ITextComponent LEAVE_GROUP = new TranslationTextComponent("message.voicechat.leave_group");
    protected static final int HEADER_SIZE = 16;
    protected static final int FOOTER_SIZE = 32;
    protected static final int UNIT_SIZE = 18;
    protected static final int CELL_HEIGHT = 36;
    protected GroupList groupList;
    protected int units;
    protected final ClientGroup group;
    protected ToggleImageButton mute;
    protected ToggleImageButton disable;
    protected ToggleImageButton showHUD;
    protected ImageButton leave;

    public GroupScreen(ClientGroup group) {
        super(TITLE, 236, 0);
        this.group = group;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft += 2;
        this.guiTop = 32;
        int minUnits = MathHelper.ceil(2.2222223f);
        this.units = Math.max(minUnits, (this.height - 16 - 32 - this.guiTop * 2) / 18);
        this.ySize = 16 + this.units * 18 + 32;
        ClientPlayerStateManager stateManager = ClientManager.getPlayerStateManager();
        if (this.groupList != null) {
            this.groupList.updateSize(this.width, this.units * 18, this.guiTop + 16);
        } else {
            this.groupList = new GroupList(this, this.width, this.units * 18, this.guiTop + 16, 36);
        }
        this.addListener(this.groupList);
        int buttonY = this.guiTop + this.ySize - 20 - 7;
        int buttonSize = 20;
        this.mute = new ToggleImageButton(this.guiLeft + 7, buttonY, MICROPHONE, stateManager::isMuted, button -> stateManager.setMuted(!stateManager.isMuted()), new MuteTooltipSupplier(this, stateManager));
        this.addButton(this.mute);
        this.disable = new ToggleImageButton(this.guiLeft + 7 + buttonSize + 3, buttonY, SPEAKER, stateManager::isDisabled, button -> stateManager.setDisabled(!stateManager.isDisabled()), new DisableTooltipSupplier(this, stateManager));
        this.addButton(this.disable);
        this.showHUD = new ToggleImageButton(this.guiLeft + 7 + (buttonSize + 3) * 2, buttonY, GROUP_HUD, VoicechatClient.CLIENT_CONFIG.showGroupHUD::get, button -> VoicechatClient.CLIENT_CONFIG.showGroupHUD.set(VoicechatClient.CLIENT_CONFIG.showGroupHUD.get() == false).save(), new HideGroupHudTooltipSupplier(this));
        this.addButton(this.showHUD);
        this.leave = new ImageButton(this.guiLeft + this.xSize - buttonSize - 7, buttonY, LEAVE, button -> {
            ClientServerNetManager.sendToServer(new LeaveGroupPacket());
            this.minecraft.displayGuiScreen(new JoinGroupScreen());
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, Collections.singletonList((IReorderingProcessor)((Object)LEAVE_GROUP)), mouseX, mouseY));
        this.addButton(this.leave);
        this.checkButtons();
    }

    @Override
    public void tick() {
        super.tick();
        this.checkButtons();
    }

    private void checkButtons() {
        this.mute.active = VoicechatClient.CLIENT_CONFIG.microphoneActivationType.get().equals((Object)MicrophoneActivationType.VOICE);
        this.showHUD.active = VoicechatClient.CLIENT_CONFIG.hideIcons.get() == false;
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
        TranslationTextComponent title = this.group.getType().equals(Group.Type.NORMAL) ? new TranslationTextComponent("message.voicechat.group_title", new StringTextComponent(this.group.getName())) : new TranslationTextComponent("message.voicechat.group_type_title", new StringTextComponent(this.group.getName()), GroupType.fromType(this.group.getType()).getTranslation());
        this.minecraft.fontRenderer.drawString(poseStack, title.getString(), this.guiLeft + this.xSize / 2 - this.font.getStringWidth(title.getString()) / 2, this.guiTop + 5, 0x404040);
        this.groupList.render(poseStack, mouseX, mouseY, delta);
    }
}
