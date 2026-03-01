package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.VoicechatApi;
import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientEntityAudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientLocationalAudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientStaticAudioChannel;
import dimasik.managers.mods.voicechat.api.config.ConfigAccessor;
import java.util.UUID;
import javax.annotation.Nullable;

public interface VoicechatClientApi
extends VoicechatApi {
    public boolean isMuted();

    public boolean isDisabled();

    public boolean isDisconnected();

    @Nullable
    public Group getGroup();

    public ClientEntityAudioChannel createEntityAudioChannel(UUID var1);

    public ClientLocationalAudioChannel createLocationalAudioChannel(UUID var1, Position var2);

    public ClientStaticAudioChannel createStaticAudioChannel(UUID var1);

    public void registerClientVolumeCategory(VolumeCategory var1);

    default public void unregisterClientVolumeCategory(VolumeCategory category) {
        this.unregisterClientVolumeCategory(category.getId());
    }

    public void unregisterClientVolumeCategory(String var1);

    public ConfigAccessor getClientConfig();
}
