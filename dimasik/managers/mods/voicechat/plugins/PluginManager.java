package dimasik.managers.mods.voicechat.plugins;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.VoicechatPlugin;
import dimasik.managers.mods.voicechat.api.VoicechatSocket;
import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.audiolistener.AudioListener;
import dimasik.managers.mods.voicechat.api.audiolistener.PlayerAudioListener;
import dimasik.managers.mods.voicechat.api.events.CreateGroupEvent;
import dimasik.managers.mods.voicechat.api.events.EntitySoundPacketEvent;
import dimasik.managers.mods.voicechat.api.events.Event;
import dimasik.managers.mods.voicechat.api.events.EventRegistration;
import dimasik.managers.mods.voicechat.api.events.JoinGroupEvent;
import dimasik.managers.mods.voicechat.api.events.LeaveGroupEvent;
import dimasik.managers.mods.voicechat.api.events.LocationalSoundPacketEvent;
import dimasik.managers.mods.voicechat.api.events.MicrophonePacketEvent;
import dimasik.managers.mods.voicechat.api.events.PlayerConnectedEvent;
import dimasik.managers.mods.voicechat.api.events.PlayerDisconnectedEvent;
import dimasik.managers.mods.voicechat.api.events.PlayerStateChangedEvent;
import dimasik.managers.mods.voicechat.api.events.RegisterVolumeCategoryEvent;
import dimasik.managers.mods.voicechat.api.events.RemoveGroupEvent;
import dimasik.managers.mods.voicechat.api.events.StaticSoundPacketEvent;
import dimasik.managers.mods.voicechat.api.events.UnregisterVolumeCategoryEvent;
import dimasik.managers.mods.voicechat.api.events.VoiceHostEvent;
import dimasik.managers.mods.voicechat.api.events.VoicechatServerStartedEvent;
import dimasik.managers.mods.voicechat.api.events.VoicechatServerStartingEvent;
import dimasik.managers.mods.voicechat.api.events.VoicechatServerStoppedEvent;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.EventBuilder;
import dimasik.managers.mods.voicechat.plugins.impl.GroupImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatConnectionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatServerApiImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatSocketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audiolistener.PlayerAudioListenerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.CreateGroupEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.EntitySoundPacketEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.JoinGroupEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.LeaveGroupEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.LocationalSoundPacketEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.MicrophonePacketEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.PlayerConnectedEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.PlayerDisconnectedEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.PlayerStateChangedEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.RegisterVolumeCategoryEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.RemoveGroupEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.StaticSoundPacketEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.UnregisterVolumeCategoryEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.VoiceHostEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.VoicechatServerStartedEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.VoicechatServerStartingEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.VoicechatServerStoppedEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.EntitySoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.LocationalSoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.MicrophonePacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.SoundPacketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.packets.StaticSoundPacketImpl;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.LocationSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.MicPacket;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import dimasik.managers.mods.voicechat.voice.server.Group;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class PluginManager {
    private List<VoicechatPlugin> plugins;
    private Map<Class<? extends Event>, List<Consumer<? extends Event>>> events;
    private Map<UUID, List<PlayerAudioListener>> playerAudioListeners;
    private static PluginManager instance;

    public void init() {
        if (this.plugins != null) {
            return;
        }
        Voicechat.LOGGER.info("Loading plugins", new Object[0]);
        this.plugins = CommonCompatibilityManager.INSTANCE.loadPlugins();
        Voicechat.LOGGER.info("Loaded {} plugin(s)", this.plugins.size());
        Voicechat.LOGGER.info("Initializing plugins", new Object[0]);
        for (VoicechatPlugin plugin : this.plugins) {
            try {
                plugin.initialize(VoicechatServerApiImpl.instance());
            }
            catch (Throwable e) {
                Voicechat.LOGGER.warn("Failed to initialize plugin '{}'", plugin.getPluginId(), e);
            }
        }
        Voicechat.LOGGER.info("Initialized {} plugin(s)", this.plugins.size());
        this.gatherEvents();
        this.playerAudioListeners = new HashMap<UUID, List<PlayerAudioListener>>();
    }

    private void gatherEvents() {
        EventBuilder eventBuilder = EventBuilder.create();
        EventRegistration registration = eventBuilder::addEvent;
        for (VoicechatPlugin plugin : this.plugins) {
            Voicechat.LOGGER.info("Registering events for '{}'", plugin.getPluginId());
            try {
                plugin.registerEvents(registration);
            }
            catch (Throwable e) {
                Voicechat.LOGGER.warn("Failed to register events for plugin '{}'", plugin.getPluginId(), e);
            }
        }
        this.events = eventBuilder.build();
    }

    public boolean registerAudioListener(AudioListener l) {
        if (!(l instanceof PlayerAudioListener)) {
            return false;
        }
        PlayerAudioListener listener = (PlayerAudioListener)l;
        boolean exists = this.playerAudioListeners.values().stream().anyMatch(listeners -> listeners.stream().anyMatch(playerAudioListener -> playerAudioListener.getListenerId().equals(listener.getListenerId())));
        if (exists) {
            return false;
        }
        this.playerAudioListeners.computeIfAbsent(listener.getPlayerUuid(), k -> new ArrayList()).add(listener);
        return true;
    }

    public boolean unregisterAudioListener(UUID listenerId) {
        boolean removed = this.playerAudioListeners.values().stream().anyMatch(listeners -> listeners.removeIf(listener -> listener.getListenerId().equals(listenerId)));
        if (!removed) {
            return false;
        }
        this.playerAudioListeners.values().removeIf(List::isEmpty);
        return true;
    }

    public List<PlayerAudioListener> getPlayerAudioListeners(UUID playerUuid) {
        return this.playerAudioListeners.getOrDefault(playerUuid, Collections.emptyList());
    }

    public void onListenerAudio(UUID playerUuid, SoundPacket<?> packet) {
        if (playerUuid.equals(packet.getSender())) {
            return;
        }
        List<PlayerAudioListener> listeners = this.getPlayerAudioListeners(playerUuid);
        if (listeners.isEmpty()) {
            return;
        }
        SoundPacketImpl soundPacket = packet instanceof GroupSoundPacket ? new StaticSoundPacketImpl((GroupSoundPacket)packet) : (packet instanceof PlayerSoundPacket ? new EntitySoundPacketImpl((PlayerSoundPacket)packet) : (packet instanceof LocationSoundPacket ? new LocationalSoundPacketImpl((LocationSoundPacket)packet) : new SoundPacketImpl(packet)));
        for (PlayerAudioListener l : listeners) {
            if (!(l instanceof PlayerAudioListenerImpl)) continue;
            ((PlayerAudioListenerImpl)l).getListener().accept(soundPacket);
        }
    }

    public <T extends Event> boolean dispatchEvent(Class<? extends T> eventClass, T event) {
        List<Consumer<? extends Event>> events = this.events.get(eventClass);
        if (events == null) {
            return false;
        }
        for (Consumer<? extends Event> evt : events) {
            try {
                Consumer<? extends Event> e = evt;
                e.accept(event);
                if (!event.isCancelled()) continue;
                break;
            }
            catch (Exception e) {
                Voicechat.LOGGER.error("Failed to dispatch event '{}'", event.getClass().getSimpleName(), e);
            }
        }
        return event.isCancelled();
    }

    public VoicechatSocket getSocketImplementation(MinecraftServer server) {
        VoicechatServerStartingEventImpl event = new VoicechatServerStartingEventImpl();
        this.dispatchEvent(VoicechatServerStartingEvent.class, event);
        VoicechatSocket socket = event.getSocketImplementation();
        if (socket == null) {
            socket = new VoicechatSocketImpl();
            Voicechat.LOGGER.debug("Using default voicechat socket implementation", new Object[0]);
        } else {
            Voicechat.LOGGER.info("Using custom voicechat socket implementation: {}", socket.getClass().getName());
        }
        return socket;
    }

    public String getVoiceHost(String voiceHost) {
        VoiceHostEventImpl event = new VoiceHostEventImpl(voiceHost);
        this.dispatchEvent(VoiceHostEvent.class, event);
        return event.getVoiceHost();
    }

    public void onRegisterVolumeCategory(VolumeCategory category) {
        this.dispatchEvent(RegisterVolumeCategoryEvent.class, new RegisterVolumeCategoryEventImpl(category));
    }

    public void onUnregisterVolumeCategory(VolumeCategory category) {
        this.dispatchEvent(UnregisterVolumeCategoryEvent.class, new UnregisterVolumeCategoryEventImpl(category));
    }

    public void onServerStarted() {
        this.dispatchEvent(VoicechatServerStartedEvent.class, new VoicechatServerStartedEventImpl());
    }

    public void onServerStopped() {
        this.dispatchEvent(VoicechatServerStoppedEvent.class, new VoicechatServerStoppedEventImpl());
    }

    public void onPlayerConnected(ServerPlayerEntity player) {
        this.dispatchEvent(PlayerConnectedEvent.class, new PlayerConnectedEventImpl(VoicechatConnectionImpl.fromPlayer(player)));
    }

    public void onPlayerDisconnected(UUID player) {
        this.dispatchEvent(PlayerDisconnectedEvent.class, new PlayerDisconnectedEventImpl(player));
    }

    public void onPlayerStateChanged(PlayerState state) {
        this.dispatchEvent(PlayerStateChangedEvent.class, new PlayerStateChangedEventImpl(state));
    }

    public boolean onJoinGroup(ServerPlayerEntity player, @Nullable Group group) {
        if (group == null) {
            return this.onLeaveGroup(player);
        }
        return this.dispatchEvent(JoinGroupEvent.class, new JoinGroupEventImpl(new GroupImpl(group), VoicechatConnectionImpl.fromPlayer(player)));
    }

    public boolean onCreateGroup(@Nullable ServerPlayerEntity player, @Nullable Group group) {
        if (group == null) {
            if (player == null) {
                return false;
            }
            return this.onLeaveGroup(player);
        }
        return this.dispatchEvent(CreateGroupEvent.class, new CreateGroupEventImpl(new GroupImpl(group), VoicechatConnectionImpl.fromPlayer(player)));
    }

    public boolean onLeaveGroup(ServerPlayerEntity player) {
        Group g;
        UUID groupUUID;
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return false;
        }
        GroupImpl group = null;
        PlayerState state = server.getPlayerStateManager().getState(player.getUniqueID());
        if (state != null && (groupUUID = state.getGroup()) != null && (g = server.getGroupManager().getGroup(groupUUID)) != null) {
            group = new GroupImpl(g);
        }
        return this.dispatchEvent(LeaveGroupEvent.class, new LeaveGroupEventImpl(group, VoicechatConnectionImpl.fromPlayer(player)));
    }

    public boolean onRemoveGroup(Group group) {
        return this.dispatchEvent(RemoveGroupEvent.class, new RemoveGroupEventImpl(new GroupImpl(group)));
    }

    public boolean onMicPacket(ServerPlayerEntity player, PlayerState state, MicPacket packet) {
        return this.dispatchEvent(MicrophonePacketEvent.class, new MicrophonePacketEventImpl(new MicrophonePacketImpl(packet, player.getUniqueID()), new VoicechatConnectionImpl(player, state)));
    }

    public boolean onSoundPacket(@Nullable ServerPlayerEntity sender, @Nullable PlayerState senderState, ServerPlayerEntity receiver, PlayerState receiverState, SoundPacket<?> p, String source) {
        VoicechatConnectionImpl senderConnection = null;
        if (sender != null && senderState != null) {
            senderConnection = new VoicechatConnectionImpl(sender, senderState);
        }
        VoicechatConnectionImpl receiverConnection = new VoicechatConnectionImpl(receiver, receiverState);
        if (p instanceof LocationSoundPacket) {
            LocationSoundPacket packet = (LocationSoundPacket)p;
            return this.dispatchEvent(LocationalSoundPacketEvent.class, new LocationalSoundPacketEventImpl(new LocationalSoundPacketImpl(packet), (VoicechatConnection)senderConnection, (VoicechatConnection)receiverConnection, source));
        }
        if (p instanceof PlayerSoundPacket) {
            PlayerSoundPacket packet = (PlayerSoundPacket)p;
            return this.dispatchEvent(EntitySoundPacketEvent.class, new EntitySoundPacketEventImpl(new EntitySoundPacketImpl(packet), (VoicechatConnection)senderConnection, (VoicechatConnection)receiverConnection, source));
        }
        if (p instanceof GroupSoundPacket) {
            GroupSoundPacket packet = (GroupSoundPacket)p;
            return this.dispatchEvent(StaticSoundPacketEvent.class, new StaticSoundPacketEventImpl(new StaticSoundPacketImpl(packet), (VoicechatConnection)senderConnection, (VoicechatConnection)receiverConnection, source));
        }
        return false;
    }

    public static PluginManager instance() {
        if (instance == null) {
            instance = new PluginManager();
            instance.init();
        }
        return instance;
    }
}
