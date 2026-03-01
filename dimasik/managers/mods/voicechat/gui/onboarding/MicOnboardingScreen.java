package dimasik.managers.mods.voicechat.gui.onboarding;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.audiodevice.SelectMicrophoneScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.DeviceOnboardingScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.SpeakerOnboardingScreen;
import dimasik.managers.mods.voicechat.voice.client.microphone.MicrophoneManager;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MicOnboardingScreen
extends DeviceOnboardingScreen {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.microphone").mergeStyle(TextFormatting.BOLD);

    public MicOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    public List<String> getNames() {
        return MicrophoneManager.deviceNames();
    }

    @Override
    public ResourceLocation getIcon() {
        return SelectMicrophoneScreen.MICROPHONE_ICON;
    }

    @Override
    public ConfigEntry<String> getConfigEntry() {
        return VoicechatClient.CLIENT_CONFIG.microphone;
    }

    @Override
    public Screen getNextScreen() {
        return new SpeakerOnboardingScreen(this);
    }
}
