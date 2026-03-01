package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.events.VolumeCategoryEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;

public class VolumeCategoryEventImpl
extends ServerEventImpl
implements VolumeCategoryEvent {
    private final VolumeCategory category;

    public VolumeCategoryEventImpl(VolumeCategory category) {
        this.category = category;
    }

    @Override
    public VolumeCategory getVolumeCategory() {
        return this.category;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}
