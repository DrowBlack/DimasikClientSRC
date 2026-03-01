package dimasik.managers.mods.voicechat.plugins;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.ClientVoicechatSocket;
import dimasik.managers.mods.voicechat.api.events.ClientReceiveSoundEvent;
import dimasik.managers.mods.voicechat.api.events.ClientSoundEvent;
import dimasik.managers.mods.voicechat.api.events.ClientVoicechatInitializationEvent;
import dimasik.managers.mods.voicechat.api.events.CreateOpenALContextEvent;
import dimasik.managers.mods.voicechat.api.events.DestroyOpenALContextEvent;
import dimasik.managers.mods.voicechat.api.events.MergeClientSoundEvent;
import dimasik.managers.mods.voicechat.api.events.OpenALSoundEvent;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.plugins.impl.ClientVoicechatSocketImpl;
import dimasik.managers.mods.voicechat.plugins.impl.PositionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientReceiveSoundEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientSoundEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientVoicechatInitializationEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.CreateOpenALContextEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.DestroyOpenALContextEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.MergeClientSoundEventImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.OpenALSoundEventImpl;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.math.vector.Vector3d;

public class ClientPluginManager {
    private final PluginManager pluginManager;
    private static ClientPluginManager instance;

    public ClientPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public ClientVoicechatSocket getClientSocketImplementation() {
        ClientVoicechatInitializationEventImpl event = new ClientVoicechatInitializationEventImpl();
        this.pluginManager.dispatchEvent(ClientVoicechatInitializationEvent.class, event);
        ClientVoicechatSocket socket = event.getSocketImplementation();
        if (socket == null) {
            socket = new ClientVoicechatSocketImpl();
            Voicechat.LOGGER.debug("Using default voicechat client socket implementation", new Object[0]);
        } else {
            Voicechat.LOGGER.info("Using custom voicechat client socket implementation: {}", socket.getClass().getName());
        }
        return socket;
    }

    @Nullable
    public short[] onMergeClientSound(@Nullable short[] rawAudio) {
        MergeClientSoundEventImpl event = new MergeClientSoundEventImpl();
        this.pluginManager.dispatchEvent(MergeClientSoundEvent.class, event);
        List<short[]> audioToMerge = event.getAudioToMerge();
        if (audioToMerge == null) {
            return rawAudio;
        }
        if (rawAudio != null) {
            audioToMerge.add(0, rawAudio);
        }
        return Utils.combineAudio(audioToMerge);
    }

    @Nullable
    public short[] onClientSound(short[] rawAudio, boolean whispering) {
        ClientSoundEventImpl clientSoundEvent = new ClientSoundEventImpl(rawAudio, whispering);
        boolean cancelled = this.pluginManager.dispatchEvent(ClientSoundEvent.class, clientSoundEvent);
        if (cancelled) {
            return null;
        }
        return clientSoundEvent.getRawAudio();
    }

    public short[] onReceiveEntityClientSound(UUID id, short[] rawAudio, boolean whispering, float distance) {
        ClientReceiveSoundEventImpl.EntitySoundImpl clientSoundEvent = new ClientReceiveSoundEventImpl.EntitySoundImpl(id, rawAudio, whispering, distance);
        this.pluginManager.dispatchEvent(ClientReceiveSoundEvent.EntitySound.class, clientSoundEvent);
        return clientSoundEvent.getRawAudio();
    }

    public short[] onReceiveLocationalClientSound(UUID id, short[] rawAudio, Vector3d pos, float distance) {
        ClientReceiveSoundEventImpl.LocationalSoundImpl clientSoundEvent = new ClientReceiveSoundEventImpl.LocationalSoundImpl(id, rawAudio, new PositionImpl(pos), distance);
        this.pluginManager.dispatchEvent(ClientReceiveSoundEvent.LocationalSound.class, clientSoundEvent);
        return clientSoundEvent.getRawAudio();
    }

    public short[] onReceiveStaticClientSound(UUID id, short[] rawAudio) {
        ClientReceiveSoundEventImpl.StaticSoundImpl clientSoundEvent = new ClientReceiveSoundEventImpl.StaticSoundImpl(id, rawAudio);
        this.pluginManager.dispatchEvent(ClientReceiveSoundEvent.StaticSound.class, clientSoundEvent);
        return clientSoundEvent.getRawAudio();
    }

    public void onALSound(int source, @Nullable UUID channelId, @Nullable Vector3d pos, @Nullable String category, Class<? extends OpenALSoundEvent> eventClass) {
        this.pluginManager.dispatchEvent(eventClass, new OpenALSoundEventImpl(channelId, pos == null ? null : new PositionImpl(pos), category, source));
    }

    public void onCreateALContext(long context, long device) {
        this.pluginManager.dispatchEvent(CreateOpenALContextEvent.class, new CreateOpenALContextEventImpl(context, device));
    }

    public void onDestroyALContext(long context, long device) {
        this.pluginManager.dispatchEvent(DestroyOpenALContextEvent.class, new DestroyOpenALContextEventImpl(context, device));
    }

    public static ClientPluginManager instance() {
        if (instance == null) {
            instance = new ClientPluginManager(PluginManager.instance());
        }
        return instance;
    }
}
