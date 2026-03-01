package dimasik.managers.mods.voicechat.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.VoiceChatScreenBase;
import dimasik.managers.mods.voicechat.gui.VoiceChatSettingsScreen;
import dimasik.managers.mods.voicechat.gui.group.GroupScreen;
import dimasik.managers.mods.voicechat.gui.group.JoinGroupScreen;
import dimasik.managers.mods.voicechat.gui.tooltips.DisableTooltipSupplier;
import dimasik.managers.mods.voicechat.gui.tooltips.HideTooltipSupplier;
import dimasik.managers.mods.voicechat.gui.tooltips.MuteTooltipSupplier;
import dimasik.managers.mods.voicechat.gui.tooltips.RecordingTooltipSupplier;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumesScreen;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import dimasik.managers.mods.voicechat.gui.widgets.ToggleImageButton;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.voice.client.AudioRecorder;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import java.util.Objects;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class VoiceChatScreen
extends VoiceChatScreenBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_voicechat.png");
    private static final ResourceLocation MICROPHONE = new ResourceLocation("main/textures/images/icons/microphone_button.png");
    private static final ResourceLocation HIDE = new ResourceLocation("main/textures/images/icons/hide_button.png");
    private static final ResourceLocation VOLUMES = new ResourceLocation("main/textures/images/icons/adjust_volumes.png");
    private static final ResourceLocation SPEAKER = new ResourceLocation("main/textures/images/icons/speaker_button.png");
    private static final ResourceLocation RECORD = new ResourceLocation("main/textures/images/icons/record_button.png");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.voice_chat.title");
    private static final ITextComponent SETTINGS = new TranslationTextComponent("message.voicechat.settings");
    private static final ITextComponent GROUP = new TranslationTextComponent("message.voicechat.group");
    private static final ITextComponent ADJUST_PLAYER_VOLUMES = new TranslationTextComponent("message.voicechat.adjust_volumes");
    private ToggleImageButton mute;
    private ToggleImageButton disable;
    private VoiceChatScreenBase.HoverArea recordingHoverArea;
    private ClientPlayerStateManager stateManager = ClientManager.getPlayerStateManager();

    public VoiceChatScreen() {
        super(TITLE, 195, 76);
    }

    @Override
    protected void init() {
        super.init();
        ClientVoicechat client = ClientManager.getClient();
        this.mute = new ToggleImageButton(this.guiLeft + 6, this.guiTop + this.ySize - 6 - 20, MICROPHONE, this.stateManager::isMuted, button -> this.stateManager.setMuted(!this.stateManager.isMuted()), new MuteTooltipSupplier(this, this.stateManager));
        this.addButton(this.mute);
        this.disable = new ToggleImageButton(this.guiLeft + 6 + 20 + 2, this.guiTop + this.ySize - 6 - 20, SPEAKER, this.stateManager::isDisabled, button -> this.stateManager.setDisabled(!this.stateManager.isDisabled()), new DisableTooltipSupplier(this, this.stateManager));
        this.addButton(this.disable);
        ImageButton volumes = new ImageButton(this.guiLeft + 6 + 20 + 2 + 20 + 2, this.guiTop + this.ySize - 6 - 20, VOLUMES, button -> this.minecraft.displayGuiScreen(new AdjustVolumesScreen()), (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, ADJUST_PLAYER_VOLUMES, mouseX, mouseY));
        this.addButton(volumes);
        if (client != null && VoicechatClient.CLIENT_CONFIG.useNatives.get().booleanValue() && (client.getRecorder() != null || client.getConnection() != null && client.getConnection().getData().allowRecording())) {
            ToggleImageButton record = new ToggleImageButton(this.guiLeft + this.xSize - 6 - 20 - 2 - 20, this.guiTop + this.ySize - 6 - 20, RECORD, () -> ClientManager.getClient() != null && ClientManager.getClient().getRecorder() != null, button -> this.toggleRecording(), new RecordingTooltipSupplier(this));
            this.addButton(record);
        }
        ToggleImageButton hide = new ToggleImageButton(this.guiLeft + this.xSize - 6 - 20, this.guiTop + this.ySize - 6 - 20, HIDE, VoicechatClient.CLIENT_CONFIG.hideIcons::get, button -> VoicechatClient.CLIENT_CONFIG.hideIcons.set(VoicechatClient.CLIENT_CONFIG.hideIcons.get() == false).save(), new HideTooltipSupplier(this));
        this.addButton(hide);
        Button settings = new Button(this.guiLeft + 6, this.guiTop + 6 + 15, 75, 20, SETTINGS, button -> this.minecraft.displayGuiScreen(new VoiceChatSettingsScreen()));
        this.addButton(settings);
        Button group = new Button(this.guiLeft + this.xSize - 6 - 75 + 1, this.guiTop + 6 + 15, 75, 20, GROUP, button -> {
            ClientGroup g = this.stateManager.getGroup();
            if (g != null) {
                this.minecraft.displayGuiScreen(new GroupScreen(g));
            } else {
                this.minecraft.displayGuiScreen(new JoinGroupScreen());
            }
        });
        this.addButton(group);
        group.active = client != null && client.getConnection() != null && client.getConnection().getData().groupsEnabled();
        this.recordingHoverArea = new VoiceChatScreenBase.HoverArea(72, this.ySize - 6 - 20, this.xSize - 122, 20);
        this.checkButtons();
    }

    @Override
    public void tick() {
        super.tick();
        this.checkButtons();
    }

    private void checkButtons() {
        this.mute.active = MuteTooltipSupplier.canMuteMic();
        this.disable.active = this.stateManager.canEnable();
    }

    private void toggleRecording() {
        ClientVoicechat c = ClientManager.getClient();
        if (c == null) {
            return;
        }
        c.toggleRecording();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ClientCompatibilityManager.INSTANCE.getBoundKeyOf(this.minecraft.gameSettings.KEY_VOICE_CHAT).getKeyCode()) {
            this.minecraft.displayGuiScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(poseStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void renderForeground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        int titleWidth = this.font.getStringWidth(TITLE.getString());
        this.minecraft.fontRenderer.drawString(poseStack, TITLE.getString(), this.guiLeft + (this.xSize - titleWidth) / 2, this.guiTop + 7, 0x404040);
        ClientVoicechat client = ClientManager.getClient();
        if (client != null && client.getRecorder() != null) {
            AudioRecorder recorder = client.getRecorder();
            StringTextComponent time = new StringTextComponent(recorder.getDuration());
            FontRenderer fontRenderer = this.minecraft.fontRenderer;
            String string = time.mergeStyle(TextFormatting.DARK_RED).getString();
            float f = (float)(this.guiLeft + this.recordingHoverArea.getPosX()) + (float)this.recordingHoverArea.getWidth() / 2.0f - (float)this.font.getStringWidth(time.getText()) / 2.0f;
            float f2 = (float)(this.guiTop + this.recordingHoverArea.getPosY()) + (float)this.recordingHoverArea.getHeight() / 2.0f;
            Objects.requireNonNull(this.minecraft.fontRenderer);
            fontRenderer.drawString(poseStack, string, f, f2 - 9.0f / 2.0f, 0);
            if (this.recordingHoverArea.isHovered(this.guiLeft, this.guiTop, mouseX, mouseY)) {
                this.renderTooltip(poseStack, new TranslationTextComponent("message.voicechat.storage_size", recorder.getStorage()), mouseX, mouseY);
            }
        }
    }
}
