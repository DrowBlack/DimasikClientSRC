package dimasik.managers.mods.voicechat.gui.audiodevice;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import dimasik.managers.mods.voicechat.gui.audiodevice.AudioDeviceEntry;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenListBase;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;

public class AudioDeviceList
extends ListScreenListBase<AudioDeviceEntry> {
    public static final int CELL_HEIGHT = 36;
    @Nullable
    protected ResourceLocation icon;
    @Nullable
    protected ConfigEntry<String> configEntry;

    public AudioDeviceList(int width, int height, int top) {
        super(width, height, top, 36);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        AudioDeviceEntry entry = (AudioDeviceEntry)this.getEntryAtPosition(mouseX, mouseY);
        if (entry == null) {
            return false;
        }
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        if (!this.isSelected(entry.getDevice())) {
            this.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            this.onSelect(entry);
            return true;
        }
        return false;
    }

    protected void onSelect(AudioDeviceEntry entry) {
        ClientVoicechat client;
        if (this.configEntry != null) {
            this.configEntry.set(entry.device).save();
        }
        if ((client = ClientManager.getClient()) != null) {
            client.reloadAudio();
        }
    }

    public AudioDeviceList setIcon(@Nullable ResourceLocation icon) {
        this.icon = icon;
        return this;
    }

    public AudioDeviceList setConfigEntry(@Nullable ConfigEntry<String> configEntry) {
        this.configEntry = configEntry;
        return this;
    }

    @Override
    public void replaceEntries(Collection<AudioDeviceEntry> entries) {
        super.replaceEntries(entries);
    }

    public void setAudioDevices(Collection<String> entries) {
        this.replaceEntries(entries.stream().map(s -> new AudioDeviceEntry((String)s, this.getVisibleName((String)s), this.icon, () -> this.isSelected((String)s))).collect(Collectors.toList()));
    }

    public boolean isSelected(String name) {
        if (this.configEntry == null) {
            return false;
        }
        return this.configEntry.get().equals(name);
    }

    public String getVisibleName(String device) {
        return SoundManager.cleanDeviceName(device);
    }

    public boolean isEmpty() {
        return this.getChildren().isEmpty();
    }
}
