package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.events.RegisterVolumeCategoryEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.VolumeCategoryEventImpl;

public class RegisterVolumeCategoryEventImpl
extends VolumeCategoryEventImpl
implements RegisterVolumeCategoryEvent {
    public RegisterVolumeCategoryEventImpl(VolumeCategory category) {
        super(category);
    }
}
