package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.Entity;
import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.ServerLevel;
import dimasik.managers.mods.voicechat.api.ServerPlayer;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.VoicechatServerApi;
import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.audiochannel.AudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.AudioPlayer;
import dimasik.managers.mods.voicechat.api.audiochannel.EntityAudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.LocationalAudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.StaticAudioChannel;
import dimasik.managers.mods.voicechat.api.audiolistener.AudioListener;
import dimasik.managers.mods.voicechat.api.audiolistener.PlayerAudioListener;
import dimasik.managers.mods.voicechat.api.audiosender.AudioSender;
import dimasik.managers.mods.voicechat.api.config.ConfigAccessor;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoder;
import dimasik.managers.mods.voicechat.api.packets.EntitySoundPacket;
import dimasik.managers.mods.voicechat.api.packets.LocationalSoundPacket;
import dimasik.managers.mods.voicechat.api.packets.StaticSoundPacket;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.plugins.impl.GroupImpl;
import dimasik.managers.mods.voicechat.plugins.impl.PositionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerLevelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerPlayerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatApiImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatConnectionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VolumeCategoryImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.AudioPlayerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.AudioSupplier;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.EntityAudioChannelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.LocationalAudioChannelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiochannel.StaticAudioChannelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiolistener.PlayerAudioListenerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiosender.AudioSenderImpl;
import dimasik.managers.mods.voicechat.plugins.impl.config.ConfigAccessorImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.EntitySoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.LocationalSoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.StaticSoundPacketImpl;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import dimasik.managers.mods.voicechat.voice.server.ClientConnection;
import dimasik.managers.mods.voicechat.voice.server.Group;
import dimasik.managers.mods.voicechat.voice.server.Server;
import dimasik.managers.mods.voicechat.voice.server.ServerWorldUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;

public class VoicechatServerApiImpl
extends VoicechatApiImpl
implements VoicechatServerApi {
    @Deprecated
    public static final VoicechatServerApiImpl INSTANCE = new VoicechatServerApiImpl();

    protected VoicechatServerApiImpl() {
    }

    public static VoicechatServerApi instance() {
        return CommonCompatibilityManager.INSTANCE.getServerApi();
    }

    @Override
    public void sendEntitySoundPacketTo(VoicechatConnection connection, EntitySoundPacket p) {
        if (p instanceof EntitySoundPacketImpl) {
            EntitySoundPacketImpl packet = (EntitySoundPacketImpl)p;
            VoicechatServerApiImpl.sendPacket(connection, packet.getPacket());
        }
    }

    @Override
    public void sendLocationalSoundPacketTo(VoicechatConnection connection, LocationalSoundPacket p) {
        if (p instanceof LocationalSoundPacketImpl) {
            LocationalSoundPacketImpl packet = (LocationalSoundPacketImpl)p;
            VoicechatServerApiImpl.sendPacket(connection, packet.getPacket());
        }
    }

    @Override
    public void sendStaticSoundPacketTo(VoicechatConnection connection, StaticSoundPacket p) {
        if (p instanceof StaticSoundPacketImpl) {
            StaticSoundPacketImpl packet = (StaticSoundPacketImpl)p;
            VoicechatServerApiImpl.sendPacket(connection, packet.getPacket());
        }
    }

    @Override
    @Nullable
    public EntityAudioChannel createEntityAudioChannel(UUID channelId, Entity entity) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return null;
        }
        return new EntityAudioChannelImpl(channelId, server, entity);
    }

    @Override
    @Nullable
    public LocationalAudioChannel createLocationalAudioChannel(UUID channelId, ServerLevel level, Position initialPosition) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return null;
        }
        if (initialPosition instanceof PositionImpl) {
            PositionImpl p = (PositionImpl)initialPosition;
            return new LocationalAudioChannelImpl(channelId, server, level, p);
        }
        throw new IllegalArgumentException("initialPosition is not an instance of PositionImpl");
    }

    @Override
    @Nullable
    public StaticAudioChannel createStaticAudioChannel(UUID channelId, ServerLevel level, VoicechatConnection connection) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return null;
        }
        if (connection instanceof VoicechatConnectionImpl) {
            VoicechatConnectionImpl conn = (VoicechatConnectionImpl)connection;
            return new StaticAudioChannelImpl(channelId, server, conn);
        }
        return null;
    }

    @Override
    public AudioPlayer createAudioPlayer(AudioChannel audioChannel, OpusEncoder encoder, Supplier<short[]> audioSupplier) {
        return new AudioPlayerImpl(audioChannel, encoder, audioSupplier);
    }

    @Override
    public AudioPlayer createAudioPlayer(AudioChannel audioChannel, OpusEncoder encoder, short[] audio) {
        return new AudioPlayerImpl(audioChannel, encoder, new AudioSupplier(audio));
    }

    @Override
    public AudioSender createAudioSender(VoicechatConnection connection) {
        return new AudioSenderImpl(connection.getPlayer().getUuid());
    }

    @Override
    public boolean registerAudioSender(AudioSender sender) {
        if (!(sender instanceof AudioSenderImpl)) {
            return false;
        }
        return AudioSenderImpl.registerAudioSender((AudioSenderImpl)sender);
    }

    @Override
    public boolean unregisterAudioSender(AudioSender sender) {
        if (!(sender instanceof AudioSenderImpl)) {
            return false;
        }
        return AudioSenderImpl.unregisterAudioSender((AudioSenderImpl)sender);
    }

    @Override
    public PlayerAudioListener.Builder playerAudioListenerBuilder() {
        return new PlayerAudioListenerImpl.BuilderImpl();
    }

    @Override
    public boolean registerAudioListener(AudioListener listener) {
        return PluginManager.instance().registerAudioListener(listener);
    }

    @Override
    public boolean unregisterAudioListener(AudioListener listener) {
        return this.unregisterAudioListener(listener.getListenerId());
    }

    @Override
    public boolean unregisterAudioListener(UUID listenerId) {
        return PluginManager.instance().unregisterAudioListener(listenerId);
    }

    public static void sendPacket(VoicechatConnection receiver, SoundPacket<?> soundPacket) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return;
        }
        PlayerState state = server.getPlayerStateManager().getState(receiver.getPlayer().getUuid());
        if (state == null) {
            return;
        }
        if (!(receiver.getPlayer() instanceof ServerPlayerImpl)) {
            throw new IllegalArgumentException("ServerPlayer is not an instance of ServerPlayerImpl");
        }
        ServerPlayerImpl serverPlayerImpl = (ServerPlayerImpl)receiver.getPlayer();
        ClientConnection c = server.getConnections().get(receiver.getPlayer().getUuid());
        server.sendSoundPacket(null, null, serverPlayerImpl.getRealServerPlayer(), state, c, soundPacket, "plugin");
    }

    @Override
    @Nullable
    public VoicechatConnection getConnectionOf(UUID playerUuid) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return null;
        }
        ServerPlayerEntity player = server.getServer().getPlayerList().getPlayerByUUID(playerUuid);
        if (player == null) {
            return null;
        }
        return VoicechatConnectionImpl.fromPlayer(player);
    }

    @Override
    public dimasik.managers.mods.voicechat.api.Group createGroup(String name, @Nullable String password) {
        return this.createGroup(name, password, false);
    }

    @Override
    public dimasik.managers.mods.voicechat.api.Group createGroup(String name, @Nullable String password, boolean persistent) {
        return this.groupBuilder().setName(name).setPassword(password).setPersistent(persistent).build();
    }

    @Override
    public Group.Builder groupBuilder() {
        return new GroupImpl.BuilderImpl();
    }

    @Override
    public boolean removeGroup(UUID groupId) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return false;
        }
        return server.getGroupManager().removeGroup(groupId);
    }

    @Override
    @Nullable
    public dimasik.managers.mods.voicechat.api.Group getGroup(UUID groupId) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return null;
        }
        return new GroupImpl(server.getGroupManager().getGroup(groupId));
    }

    @Override
    public Collection<dimasik.managers.mods.voicechat.api.Group> getGroups() {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return Collections.emptyList();
        }
        return server.getGroupManager().getGroups().values().stream().map(group -> new GroupImpl((Group)group)).collect(Collectors.toList());
    }

    @Override
    @Nullable
    public UUID getSecret(UUID userId) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return null;
        }
        return server.getSecret(userId);
    }

    @Override
    public Collection<ServerPlayer> getPlayersInRange(ServerLevel level, Position pos, double range, @Nullable Predicate<ServerPlayer> filter) {
        if (!(pos instanceof PositionImpl)) {
            throw new IllegalArgumentException("Position is not an instance of PositionImpl");
        }
        PositionImpl p = (PositionImpl)pos;
        if (!(level instanceof ServerLevelImpl)) {
            throw new IllegalArgumentException("ServerLevel is not an instance of ServerLevelImpl");
        }
        ServerLevelImpl serverLevel = (ServerLevelImpl)level;
        return ServerWorldUtils.getPlayersInRange(serverLevel.getRawServerLevel(), p.getPosition(), range, filter == null ? null : player -> filter.test(new ServerPlayerImpl((ServerPlayerEntity)player))).stream().map(ServerPlayerImpl::new).collect(Collectors.toList());
    }

    @Override
    public double getBroadcastRange() {
        return Math.max(Voicechat.SERVER_CONFIG.voiceChatDistance.get(), Voicechat.SERVER_CONFIG.broadcastRange.get());
    }

    @Override
    public void registerVolumeCategory(VolumeCategory category) {
        if (!(category instanceof VolumeCategoryImpl)) {
            throw new IllegalArgumentException("VolumeCategory is not an instance of VolumeCategoryImpl");
        }
        VolumeCategoryImpl c = (VolumeCategoryImpl)category;
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return;
        }
        server.getCategoryManager().addCategory(c);
        PluginManager.instance().onRegisterVolumeCategory(category);
    }

    @Override
    public void unregisterVolumeCategory(String categoryId) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return;
        }
        VolumeCategoryImpl category = server.getCategoryManager().removeCategory(categoryId);
        if (category != null) {
            PluginManager.instance().onUnregisterVolumeCategory(category);
        }
    }

    @Override
    public Collection<VolumeCategory> getVolumeCategories() {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return Collections.emptyList();
        }
        return server.getCategoryManager().getCategories().stream().map(VolumeCategory.class::cast).collect(Collectors.toList());
    }

    @Override
    public ConfigAccessor getServerConfig() {
        return new ConfigAccessorImpl(Voicechat.SERVER_CONFIG.voiceChatDistance.getConfig());
    }
}
