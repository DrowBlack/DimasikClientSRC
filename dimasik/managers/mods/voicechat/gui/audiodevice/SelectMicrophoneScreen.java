package dimasik.managers.mods.voicechat.gui.audiodevice;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.audiodevice.SelectDeviceScreen;
import dimasik.managers.mods.voicechat.voice.client.microphone.MicrophoneManager;
import java.awt.Color;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class SelectMicrophoneScreen
extends SelectDeviceScreen {
    public static final ResourceLocation MICROPHONE_ICON = new ResourceLocation("main/textures/images/icons/microphone.png");
    public static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.select_microphone.title");
    public static final ITextComponent NO_MICROPHONE = new TranslationTextComponent("message.voicechat.no_microphone").setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())));

    public SelectMicrophoneScreen(@Nullable Screen parent) {
        super(TITLE, parent);
    }

    @Override
    public List<String> getDevices() {
        return MicrophoneManager.deviceNames();
    }

    @Override
    public ResourceLocation getIcon() {
        return MICROPHONE_ICON;
    }

    @Override
    public ITextComponent getEmptyListComponent() {
        return NO_MICROPHONE;
    }

    @Override
    public ConfigEntry<String> getConfigEntry() {
        return VoicechatClient.CLIENT_CONFIG.microphone;
    }
}
