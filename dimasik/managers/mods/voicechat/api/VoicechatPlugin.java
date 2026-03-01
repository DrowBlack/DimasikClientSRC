package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.VoicechatApi;
import dimasik.managers.mods.voicechat.api.events.EventRegistration;

public interface VoicechatPlugin {
    public String getPluginId();

    default public void initialize(VoicechatApi api) {
    }

    default public void registerEvents(EventRegistration registration) {
    }
}
