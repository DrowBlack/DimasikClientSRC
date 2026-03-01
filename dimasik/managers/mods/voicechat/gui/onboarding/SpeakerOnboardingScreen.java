package dimasik.managers.mods.voicechat.gui.onboarding;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.audiodevice.SelectSpeakerScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.ActivationOnboardingScreen;
import dimasik.managers.mods.voicechat.gui.onboarding.DeviceOnboardingScreen;
import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class SpeakerOnboardingScreen
extends DeviceOnboardingScreen {
    private static final ITextComponent TITLE = new TranslationTextComponent("message.voicechat.onboarding.speaker").mergeStyle(TextFormatting.BOLD);

    public SpeakerOnboardingScreen(@Nullable Screen previous) {
        super(TITLE, previous);
    }

    @Override
    public List<String> getNames() {
        return SoundManager.getAllSpeakers();
    }

    @Override
    public ResourceLocation getIcon() {
        return SelectSpeakerScreen.SPEAKER_ICON;
    }

    @Override
    public ConfigEntry<String> getConfigEntry() {
        return VoicechatClient.CLIENT_CONFIG.speaker;
    }

    @Override
    public Screen getNextScreen() {
        return new ActivationOnboardingScreen(this);
    }
}
