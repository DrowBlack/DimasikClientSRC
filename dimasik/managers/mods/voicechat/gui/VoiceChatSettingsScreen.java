package dimasik.managers.mods.voicechat.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.VoiceChatScreenBase;
import dimasik.managers.mods.voicechat.gui.audiodevice.SelectMicrophoneScreen;
import dimasik.managers.mods.voicechat.gui.audiodevice.SelectSpeakerScreen;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumesScreen;
import dimasik.managers.mods.voicechat.gui.widgets.DenoiserButton;
import dimasik.managers.mods.voicechat.gui.widgets.EnumButton;
import dimasik.managers.mods.voicechat.gui.widgets.KeybindButton;
import dimasik.managers.mods.voicechat.gui.widgets.MicActivationButton;
import dimasik.managers.mods.voicechat.gui.widgets.MicAmplificationSlider;
import dimasik.managers.mods.voicechat.gui.widgets.MicTestButton;
import dimasik.managers.mods.voicechat.gui.widgets.VoiceActivationSlider;
import dimasik.managers.mods.voicechat.gui.widgets.VoiceSoundSlider;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import dimasik.managers.mods.voicechat.voice.client.speaker.AudioType;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class VoiceChatSettingsScreen
extends VoiceChatScreenBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_voicechat_settings.png");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.voice_chat_settings.title");
    private static final ITextComponent ASSIGN_TOOLTIP = new TranslationTextComponent("message.voicechat.press_to_reassign_key");
    private static final ITextComponent PUSH_TO_TALK = new TranslationTextComponent("message.voicechat.activation_type.ptt");
    private static final ITextComponent ADJUST_VOLUMES = new TranslationTextComponent("message.voicechat.adjust_volumes");
    private static final ITextComponent SELECT_MICROPHONE = new TranslationTextComponent("message.voicechat.select_microphone");
    private static final ITextComponent SELECT_SPEAKER = new TranslationTextComponent("message.voicechat.select_speaker");
    private static final ITextComponent BACK = new TranslationTextComponent("message.voicechat.back");
    @Nullable
    private final Screen parent;
    private VoiceActivationSlider voiceActivationSlider;
    private MicTestButton micTestButton;
    private KeybindButton keybindButton;

    public VoiceChatSettingsScreen(@Nullable Screen parent) {
        super(TITLE, 248, 219);
        this.parent = parent;
    }

    public VoiceChatSettingsScreen() {
        this((Screen)null);
    }

    @Override
    protected void init() {
        super.init();
        int y = this.guiTop + 20;
        this.addButton(new VoiceSoundSlider(this.guiLeft + 10, y, this.xSize - 20, 20));
        this.addButton(new MicAmplificationSlider(this.guiLeft + 10, y += 21, this.xSize - 20, 20));
        this.addButton(new DenoiserButton(this.guiLeft + 10, y += 21, this.xSize - 20, 20));
        this.voiceActivationSlider = new VoiceActivationSlider(this.guiLeft + 10 + 20 + 1, (y += 21) + 21, this.xSize - 20 - 20 - 1, 20);
        this.micTestButton = new MicTestButton(this.guiLeft + 10, y + 21, this.voiceActivationSlider);
        this.keybindButton = new KeybindButton(this.minecraft.gameSettings.KEY_PTT, this.guiLeft + 10 + 20 + 1, y + 21, this.xSize - 20 - 20 - 1, 20, PUSH_TO_TALK);
        this.addButton(new MicActivationButton(this.guiLeft + 10, y, this.xSize - 20, 20, type -> {
            this.voiceActivationSlider.visible = MicrophoneActivationType.VOICE.equals(type);
            this.keybindButton.visible = MicrophoneActivationType.PTT.equals(type);
        }));
        this.addButton(this.micTestButton);
        this.addButton(this.voiceActivationSlider);
        this.addButton(this.keybindButton);
        this.addButton(new EnumButton<AudioType>(this.guiLeft + 10, y += 42, this.xSize - 20, 20, VoicechatClient.CLIENT_CONFIG.audioType){

            @Override
            protected ITextComponent getText(AudioType type) {
                return new TranslationTextComponent("message.voicechat.audio_type", type.getText());
            }

            @Override
            protected void onUpdate(AudioType type) {
                ClientVoicechat client = ClientManager.getClient();
                if (client != null) {
                    VoiceChatSettingsScreen.this.micTestButton.stop();
                    client.reloadAudio();
                }
            }
        });
        y += 21;
        if (this.isIngame()) {
            this.addButton(new Button(this.guiLeft + 10, y, this.xSize - 20, 20, ADJUST_VOLUMES, button -> this.minecraft.displayGuiScreen(new AdjustVolumesScreen())));
            y += 21;
        }
        this.addButton(new Button(this.guiLeft + 10, y, this.xSize / 2 - 15, 20, SELECT_MICROPHONE, button -> this.minecraft.displayGuiScreen(new SelectMicrophoneScreen(this))));
        this.addButton(new Button(this.guiLeft + this.xSize / 2 + 1, y, (this.xSize - 20) / 2 - 1, 20, SELECT_SPEAKER, button -> this.minecraft.displayGuiScreen(new SelectSpeakerScreen(this))));
        y += 21;
        if (!this.isIngame() && this.parent != null) {
            this.addButton(new Button(this.guiLeft + 10, y, this.xSize - 20, 20, BACK, button -> this.minecraft.displayGuiScreen(this.parent)));
        }
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        if (this.isIngame()) {
            this.blit(poseStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        }
    }

    @Override
    public void renderForeground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        int titleWidth = this.font.getStringWidth(TITLE.getString());
        this.minecraft.fontRenderer.drawString(poseStack, TITLE.getString(), this.guiLeft + (this.xSize - titleWidth) / 2, this.guiTop + 7, this.getFontColor());
        ITextComponent sliderTooltip = this.voiceActivationSlider.getHoverText();
        if (this.voiceActivationSlider.isHovered() && sliderTooltip != null) {
            this.renderTooltip(poseStack, sliderTooltip, mouseX, mouseY);
        } else if (this.micTestButton.isHovered()) {
            this.micTestButton.renderToolTip(poseStack, mouseX, mouseY);
        } else if (this.keybindButton.isHovered()) {
            this.renderTooltip(poseStack, ASSIGN_TOOLTIP, mouseX, mouseY);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.keybindButton.isListening()) {
            return false;
        }
        return super.shouldCloseOnEsc();
    }
}
