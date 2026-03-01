package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.events.UnregisterVolumeCategoryEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.VolumeCategoryEventImpl;

public class UnregisterVolumeCategoryEventImpl
extends VolumeCategoryEventImpl
implements UnregisterVolumeCategoryEvent {
    public UnregisterVolumeCategoryEventImpl(VolumeCategory category) {
        super(category);
    }
}
