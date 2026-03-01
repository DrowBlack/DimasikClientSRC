package dimasik.managers.mods.voicechat.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.debug.VoicechatUncaughtExceptionHandler;
import dimasik.managers.mods.voicechat.gui.widgets.ImageButton;
import dimasik.managers.mods.voicechat.gui.widgets.ToggleImageButton;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.MicActivator;
import dimasik.managers.mods.voicechat.voice.client.MicThread;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneException;
import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import dimasik.managers.mods.voicechat.voice.client.speaker.Speaker;
import dimasik.managers.mods.voicechat.voice.client.speaker.SpeakerException;
import dimasik.managers.mods.voicechat.voice.client.speaker.SpeakerManager;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.awt.Color;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class MicTestButton
extends ToggleImageButton
implements ImageButton.TooltipSupplier {
    private static final ResourceLocation MICROPHONE = new ResourceLocation("main/textures/images/icons/microphone_button.png");
    private static final ITextComponent TEST_DISABLED = new TranslationTextComponent("message.voicechat.mic_test.disabled");
    private static final ITextComponent TEST_ENABLED = new TranslationTextComponent("message.voicechat.mic_test.enabled");
    private static final ITextComponent TEST_UNAVAILABLE = new TranslationTextComponent("message.voicechat.mic_test_unavailable").setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.RED.getRGB())));
    private boolean micActive;
    @Nullable
    private VoiceThread voiceThread;
    private final MicListener micListener;
    @Nullable
    private final ClientVoicechat client;

    public MicTestButton(int xIn, int yIn, MicListener micListener) {
        super(xIn, yIn, MICROPHONE, null, null, null);
        this.micListener = micListener;
        this.client = ClientManager.getClient();
        this.active = this.client == null || this.client.getSoundManager() != null;
        this.stateSupplier = () -> !this.micActive;
        this.tooltipSupplier = this;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        super.render(matrixStack, x, y, partialTicks);
        if (this.visible && this.voiceThread != null) {
            this.voiceThread.updateLastRender();
        }
    }

    @Override
    protected boolean shouldRenderTooltip() {
        return false;
    }

    public void setMicActive(boolean micActive) {
        this.micActive = micActive;
    }

    @Override
    public boolean isHovered() {
        return this.isHovered;
    }

    @Override
    public void onPress() {
        this.setMicActive(!this.micActive);
        if (this.micActive) {
            this.close();
            try {
                this.voiceThread = new VoiceThread();
                this.voiceThread.start();
            }
            catch (Exception e) {
                this.setMicActive(false);
                this.active = false;
                Voicechat.LOGGER.error("Microphone error", e);
            }
        } else {
            this.close();
        }
    }

    private void close() {
        if (this.voiceThread != null) {
            this.voiceThread.close();
            this.voiceThread = null;
        }
    }

    public void stop() {
        this.close();
        this.setMicActive(false);
    }

    @Override
    public void onTooltip(ImageButton button, MatrixStack matrices, int mouseX, int mouseY) {
        Screen screen = this.mc.currentScreen;
        if (screen == null) {
            return;
        }
        if (!this.active) {
            screen.renderTooltip(matrices, TEST_UNAVAILABLE, mouseX, mouseY);
            return;
        }
        if (this.micActive) {
            screen.renderTooltip(matrices, TEST_ENABLED, mouseX, mouseY);
        } else {
            screen.renderTooltip(matrices, TEST_DISABLED, mouseX, mouseY);
        }
    }

    public static interface MicListener {
        public void onMicValue(double var1);
    }

    private class VoiceThread
    extends Thread {
        private final MicActivator micActivator;
        private final Speaker speaker;
        private boolean running = true;
        private long lastRender;
        private MicThread micThread;
        private boolean usesOwnMicThread;
        @Nullable
        private SoundManager ownSoundManager;

        public VoiceThread() throws SpeakerException, MicrophoneException {
            SoundManager soundManager;
            this.setDaemon(true);
            this.setName("VoiceTestingThread");
            this.setUncaughtExceptionHandler(new VoicechatUncaughtExceptionHandler());
            this.micActivator = new MicActivator();
            MicThread micThread = this.micThread = MicTestButton.this.client != null ? MicTestButton.this.client.getMicThread() : null;
            if (this.micThread == null) {
                this.micThread = new MicThread(MicTestButton.this.client, null);
                this.usesOwnMicThread = true;
            }
            if (MicTestButton.this.client == null) {
                this.ownSoundManager = soundManager = new SoundManager(VoicechatClient.CLIENT_CONFIG.speaker.get());
            } else {
                soundManager = MicTestButton.this.client.getSoundManager();
            }
            if (soundManager == null) {
                throw new SpeakerException("No sound manager");
            }
            this.speaker = SpeakerManager.createSpeaker(soundManager, null);
            this.updateLastRender();
            this.setMicLocked(true);
        }

        @Override
        public void run() {
            while (this.running && System.currentTimeMillis() - this.lastRender <= 500L) {
                short[] buff = this.micThread.pollMic();
                if (buff == null) continue;
                MicTestButton.this.micListener.onMicValue(Utils.dbToPerc(Utils.getHighestAudioLevel(buff)));
                if (VoicechatClient.CLIENT_CONFIG.microphoneActivationType.get().equals((Object)MicrophoneActivationType.VOICE)) {
                    if (!this.micActivator.push(buff, a -> {})) continue;
                    this.play(buff);
                    continue;
                }
                this.micActivator.stopActivating();
                this.play(buff);
            }
            this.speaker.close();
            this.setMicLocked(false);
            MicTestButton.this.micListener.onMicValue(0.0);
            if (this.usesOwnMicThread) {
                this.micThread.close();
            }
            if (this.ownSoundManager != null) {
                this.ownSoundManager.close();
            }
            MicTestButton.this.setMicActive(false);
            Voicechat.LOGGER.info("Mic test audio channel closed", new Object[0]);
        }

        private void play(short[] buff) {
            this.speaker.play(buff, VoicechatClient.CLIENT_CONFIG.voiceChatVolume.get().floatValue(), null);
        }

        public void updateLastRender() {
            this.lastRender = System.currentTimeMillis();
        }

        private void setMicLocked(boolean locked) {
            this.micThread.setMicrophoneLocked(locked);
        }

        public void close() {
            if (!this.running) {
                return;
            }
            Voicechat.LOGGER.info("Stopping mic test audio channel", new Object[0]);
            this.running = false;
            try {
                this.join();
            }
            catch (InterruptedException e) {
                Voicechat.LOGGER.warn("Failed to close microphone", e);
            }
        }
    }
}
