package dimasik.managers.mods.voicechat.gui.volume;

import com.google.common.collect.Lists;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumesScreen;
import dimasik.managers.mods.voicechat.gui.volume.CategoryVolumeEntry;
import dimasik.managers.mods.voicechat.gui.volume.PlayerVolumeEntry;
import dimasik.managers.mods.voicechat.gui.volume.VolumeEntry;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenListBase;
import dimasik.managers.mods.voicechat.plugins.impl.VolumeCategoryImpl;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

public class AdjustVolumeList
extends ListScreenListBase<VolumeEntry> {
    protected AdjustVolumesScreen screen;
    protected final List<VolumeEntry> entries;
    protected String filter;

    public AdjustVolumeList(int width, int height, int top, int size, AdjustVolumesScreen screen) {
        super(width, height, top, size);
        this.screen = screen;
        this.entries = Lists.newArrayList();
        this.filter = "";
        this.updateEntryList();
    }

    public static void update() {
        if (Minecraft.getInstance().currentScreen instanceof AdjustVolumesScreen) {
            ((AdjustVolumesScreen)Minecraft.getInstance().currentScreen).volumeList.updateEntryList();
        }
    }

    public void updateEntryList() {
        List<PlayerState> onlinePlayers = ClientManager.getPlayerStateManager().getPlayerStates(false);
        this.entries.clear();
        for (VolumeCategoryImpl category : ClientManager.getCategoryManager().getCategories()) {
            this.entries.add(new CategoryVolumeEntry(category, this.screen));
        }
        for (PlayerState state : onlinePlayers) {
            this.entries.add(new PlayerVolumeEntry(state, this.screen));
        }
        if (VoicechatClient.CLIENT_CONFIG.offlinePlayerVolumeAdjustment.get().booleanValue()) {
            this.addOfflinePlayers(onlinePlayers);
        }
        this.updateFilter();
    }

    private void addOfflinePlayers(Collection<PlayerState> onlinePlayers) {
        for (UUID uuid : VoicechatClient.VOLUME_CONFIG.getPlayerVolumes().keySet()) {
            String name;
            if (uuid.equals(Util.DUMMY_UUID) || onlinePlayers.stream().anyMatch(state -> uuid.equals(state.getUuid())) || (name = VoicechatClient.USERNAME_CACHE.getUsername(uuid)) == null) continue;
            this.entries.add(new PlayerVolumeEntry(new PlayerState(uuid, name, false, true), this.screen));
        }
    }

    public void updateFilter() {
        this.clearEntries();
        ArrayList<VolumeEntry> filteredEntries = new ArrayList<VolumeEntry>(this.entries);
        if (!this.filter.isEmpty()) {
            filteredEntries.removeIf(volumeEntry -> {
                if (volumeEntry instanceof PlayerVolumeEntry) {
                    PlayerVolumeEntry playerVolumeEntry = (PlayerVolumeEntry)volumeEntry;
                    return playerVolumeEntry.getState() == null || !playerVolumeEntry.getState().getName().toLowerCase(Locale.ROOT).contains(this.filter);
                }
                if (volumeEntry instanceof CategoryVolumeEntry) {
                    CategoryVolumeEntry categoryVolumeEntry = (CategoryVolumeEntry)volumeEntry;
                    return !categoryVolumeEntry.getCategory().getName().toLowerCase(Locale.ROOT).contains(this.filter);
                }
                return true;
            });
        }
        filteredEntries.sort((e1, e2) -> {
            if (!e1.getClass().equals(e2.getClass())) {
                if (e1 instanceof PlayerVolumeEntry) {
                    return 1;
                }
                return -1;
            }
            return this.volumeEntryToString((VolumeEntry)e1).compareToIgnoreCase(this.volumeEntryToString((VolumeEntry)e2));
        });
        if (this.filter.isEmpty()) {
            filteredEntries.add(0, new PlayerVolumeEntry(null, this.screen));
        }
        this.replaceEntries(filteredEntries);
    }

    private String volumeEntryToString(VolumeEntry entry) {
        if (entry instanceof PlayerVolumeEntry) {
            PlayerVolumeEntry playerVolumeEntry = (PlayerVolumeEntry)entry;
            return playerVolumeEntry.getState() == null ? "" : playerVolumeEntry.getState().getName();
        }
        if (entry instanceof CategoryVolumeEntry) {
            CategoryVolumeEntry categoryVolumeEntry = (CategoryVolumeEntry)entry;
            return categoryVolumeEntry.getCategory().getName();
        }
        return "";
    }

    public void setFilter(String filter) {
        this.filter = filter;
        this.updateFilter();
    }

    public boolean isEmpty() {
        return this.getChildren().isEmpty();
    }
}
