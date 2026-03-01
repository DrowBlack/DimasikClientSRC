package dimasik.managers.mods.voicechat.gui.audiodevice;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.audiodevice.SelectDeviceScreen;
import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import java.awt.Color;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class SelectSpeakerScreen
extends SelectDeviceScreen {
    public static final ResourceLocation SPEAKER_ICON = new ResourceLocation("main/textures/images/icons/speaker.png");
    public static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.select_speaker.title");
    public static final ITextComponent NO_SPEAKER = new TranslationTextComponent("message.voicechat.no_speaker").setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())));

    public SelectSpeakerScreen(@Nullable Screen parent) {
        super(TITLE, parent);
    }

    @Override
    public List<String> getDevices() {
        return SoundManager.getAllSpeakers();
    }

    @Override
    public ResourceLocation getIcon() {
        return SPEAKER_ICON;
    }

    @Override
    public ITextComponent getEmptyListComponent() {
        return NO_SPEAKER;
    }

    @Override
    public ConfigEntry<String> getConfigEntry() {
        return VoicechatClient.CLIENT_CONFIG.speaker;
    }
}
