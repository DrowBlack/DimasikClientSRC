package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.VoicechatClientApi;
import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientEntityAudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientLocationalAudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientStaticAudioChannel;
import dimasik.managers.mods.voicechat.api.config.ConfigAccessor;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.impl.ClientGroupImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatApiImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VolumeCategoryImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.ClientEntityAudioChannelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.ClientLocationalAudioChannelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.ClientStaticAudioChannelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.config.ConfigAccessorImpl;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientUtils;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import java.util.UUID;
import javax.annotation.Nullable;

public class VoicechatClientApiImpl
extends VoicechatApiImpl
implements VoicechatClientApi {
    @Deprecated
    public static final VoicechatClientApiImpl INSTANCE = new VoicechatClientApiImpl();

    private VoicechatClientApiImpl() {
    }

    public static VoicechatClientApi instance() {
        return ClientCompatibilityManager.INSTANCE.getClientApi();
    }

    @Override
    public boolean isMuted() {
        return ClientManager.getPlayerStateManager().isMuted();
    }

    @Override
    public boolean isDisabled() {
        return ClientManager.getPlayerStateManager().isDisabled();
    }

    @Override
    public boolean isDisconnected() {
        return ClientManager.getPlayerStateManager().isDisconnected();
    }

    @Override
    @Nullable
    public Group getGroup() {
        ClientPlayerStateManager playerStateManager = ClientManager.getPlayerStateManager();
        if (playerStateManager.getGroupID() == null) {
            return null;
        }
        ClientGroup group = playerStateManager.getGroup();
        if (group == null) {
            return null;
        }
        return new ClientGroupImpl(group);
    }

    @Override
    public ClientEntityAudioChannel createEntityAudioChannel(UUID uuid) {
        return new ClientEntityAudioChannelImpl(uuid);
    }

    @Override
    public ClientLocationalAudioChannel createLocationalAudioChannel(UUID uuid, Position position) {
        return new ClientLocationalAudioChannelImpl(uuid, position);
    }

    @Override
    public ClientStaticAudioChannel createStaticAudioChannel(UUID uuid) {
        return new ClientStaticAudioChannelImpl(uuid);
    }

    @Override
    public void unregisterClientVolumeCategory(String categoryId) {
        ClientManager.getCategoryManager().removeCategory(categoryId);
    }

    @Override
    public ConfigAccessor getClientConfig() {
        return new ConfigAccessorImpl(VoicechatClient.CLIENT_CONFIG.disabled.getConfig());
    }

    @Override
    public void registerClientVolumeCategory(VolumeCategory category) {
        if (!(category instanceof VolumeCategoryImpl)) {
            throw new IllegalArgumentException("VolumeCategory is not an instance of VolumeCategoryImpl");
        }
        VolumeCategoryImpl c = (VolumeCategoryImpl)category;
        ClientManager.getCategoryManager().addCategory(c);
    }

    @Override
    public double getVoiceChatDistance() {
        return ClientUtils.getDefaultDistanceClient();
    }
}
