package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.events.ServerEvent;

public interface VolumeCategoryEvent
extends ServerEvent {
    public VolumeCategory getVolumeCategory();
}
