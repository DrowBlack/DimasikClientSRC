package dimasik.managers.mods.voicechat.voice.client.speaker;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.api.events.OpenALSoundEvent;
import dimasik.managers.mods.voicechat.plugins.ClientPluginManager;
import dimasik.managers.mods.voicechat.voice.client.ClientUtils;
import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import dimasik.managers.mods.voicechat.voice.client.speaker.Speaker;
import dimasik.managers.mods.voicechat.voice.client.speaker.SpeakerException;
import dimasik.managers.mods.voicechat.voice.common.NamedThreadPoolFactory;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.openal.AL11;

public abstract class ALSpeakerBase
implements Speaker {
    protected final Minecraft mc = Minecraft.getInstance();
    protected final SoundManager soundManager;
    protected final int sampleRate;
    protected int bufferSize;
    protected int bufferSampleSize;
    protected int source;
    protected volatile int bufferIndex;
    protected final int[] buffers;
    protected final ExecutorService executor;
    @Nullable
    protected UUID audioChannelId;

    public ALSpeakerBase(SoundManager soundManager, int sampleRate, int bufferSize, @Nullable UUID audioChannelId) {
        this.soundManager = soundManager;
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
        this.bufferSampleSize = bufferSize;
        this.audioChannelId = audioChannelId;
        this.buffers = new int[32];
        Object threadName = audioChannelId == null ? "SoundSourceThread" : "SoundSourceThread-" + String.valueOf(audioChannelId);
        this.executor = Executors.newSingleThreadExecutor(NamedThreadPoolFactory.create((String)threadName));
    }

    @Override
    public void open() throws SpeakerException {
        this.runInContext(this::openSync);
    }

    protected void openSync() {
        if (this.hasValidSourceSync()) {
            return;
        }
        this.source = AL11.alGenSources();
        SoundManager.checkAlError();
        AL11.alSourcei(this.source, 4103, 0);
        SoundManager.checkAlError();
        AL11.alDistanceModel(53251);
        SoundManager.checkAlError();
        AL11.alSourcef(this.source, 4131, ClientUtils.getDefaultDistanceClient());
        SoundManager.checkAlError();
        AL11.alSourcef(this.source, 4128, 0.0f);
        SoundManager.checkAlError();
        AL11.alGenBuffers(this.buffers);
        SoundManager.checkAlError();
    }

    @Override
    public void play(short[] data, float volume, @Nullable Vector3d position, @Nullable String category, float maxDistance) {
        this.runInContext(() -> {
            this.removeProcessedBuffersSync();
            boolean stopped = this.isStoppedSync();
            if (stopped) {
                Voicechat.LOGGER.debug("Filling playback buffer {}", this.audioChannelId);
                for (int i = 0; i < this.getBufferSize(); ++i) {
                    this.writeSync(new short[this.bufferSize], 1.0f, position, category, maxDistance);
                }
            }
            this.writeSync(data, volume, position, category, maxDistance);
            if (stopped) {
                AL11.alSourcePlay(this.source);
                SoundManager.checkAlError();
            }
        });
    }

    protected boolean isStoppedSync() {
        return this.getStateSync() == 4113 || this.getStateSync() == 4116 || this.getQueuedBuffersSync() <= 0;
    }

    protected int getBufferSize() {
        return VoicechatClient.CLIENT_CONFIG.outputBufferSize.get();
    }

    protected void writeSync(short[] data, float volume, @Nullable Vector3d position, @Nullable String category, float maxDistance) {
        ClientPluginManager.instance().onALSound(this.source, this.audioChannelId, position, category, OpenALSoundEvent.Pre.class);
        this.setPositionSync(position, maxDistance);
        ClientPluginManager.instance().onALSound(this.source, this.audioChannelId, position, category, OpenALSoundEvent.class);
        AL11.alSourcef(this.source, 4110, 6.0f);
        SoundManager.checkAlError();
        AL11.alSourcef(this.source, 4106, this.getVolume(volume, position, maxDistance));
        SoundManager.checkAlError();
        AL11.alListenerf(4106, 1.0f);
        SoundManager.checkAlError();
        int queuedBuffers = this.getQueuedBuffersSync();
        if (queuedBuffers >= this.buffers.length) {
            Voicechat.LOGGER.warn("Full playback buffer: {}/{}", queuedBuffers, this.buffers.length);
            int sampleOffset = AL11.alGetSourcei(this.source, 4133);
            SoundManager.checkAlError();
            int buffersToSkip = queuedBuffers - this.getBufferSize();
            AL11.alSourcei(this.source, 4133, sampleOffset + buffersToSkip * this.bufferSampleSize);
            SoundManager.checkAlError();
            this.removeProcessedBuffersSync();
        }
        AL11.alBufferData(this.buffers[this.bufferIndex], this.getFormat(), this.convert(data, position), this.sampleRate);
        SoundManager.checkAlError();
        AL11.alSourceQueueBuffers(this.source, this.buffers[this.bufferIndex]);
        SoundManager.checkAlError();
        this.bufferIndex = (this.bufferIndex + 1) % this.buffers.length;
        ClientPluginManager.instance().onALSound(this.source, this.audioChannelId, position, category, OpenALSoundEvent.Post.class);
    }

    protected float getVolume(float volume, @Nullable Vector3d position, float maxDistance) {
        return volume;
    }

    protected void linearAttenuation(float maxDistance) {
        AL11.alDistanceModel(53251);
        SoundManager.checkAlError();
        AL11.alSourcef(this.source, 4131, maxDistance);
        SoundManager.checkAlError();
        AL11.alSourcef(this.source, 4128, maxDistance / 2.0f);
        SoundManager.checkAlError();
    }

    protected abstract int getFormat();

    protected short[] convert(short[] data, @Nullable Vector3d position) {
        return data;
    }

    protected void setPositionSync(@Nullable Vector3d soundPos, float maxDistance) {
        ActiveRenderInfo camera = this.mc.gameRenderer.getActiveRenderInfo();
        Vector3d position = camera.getProjectedView();
        Vector3f look = camera.getViewVector();
        Vector3f up = camera.getUpVector();
        AL11.alListener3f(4100, (float)position.x, (float)position.y, (float)position.z);
        SoundManager.checkAlError();
        AL11.alListenerfv(4111, new float[]{look.getX(), look.getY(), look.getZ(), up.getX(), up.getY(), up.getZ()});
        SoundManager.checkAlError();
        if (soundPos != null) {
            this.linearAttenuation(maxDistance);
            AL11.alSourcei(this.source, 514, 0);
            SoundManager.checkAlError();
            AL11.alSource3f(this.source, 4100, (float)soundPos.x, (float)soundPos.y, (float)soundPos.z);
            SoundManager.checkAlError();
        } else {
            this.linearAttenuation(48.0f);
            AL11.alSourcei(this.source, 514, 1);
            SoundManager.checkAlError();
            AL11.alSource3f(this.source, 4100, 0.0f, 0.0f, 0.0f);
            SoundManager.checkAlError();
        }
    }

    @Override
    public void close() {
        this.runInContext(this::closeSync);
    }

    protected void closeSync() {
        if (this.hasValidSourceSync()) {
            if (this.getStateSync() == 4114) {
                AL11.alSourceStop(this.source);
                SoundManager.checkAlError();
            }
            AL11.alDeleteSources(this.source);
            SoundManager.checkAlError();
            AL11.alDeleteBuffers(this.buffers);
            SoundManager.checkAlError();
        }
        this.source = 0;
        this.executor.shutdown();
    }

    public void checkBufferEmpty(Runnable onEmpty) {
        this.runInContext(() -> {
            if (this.getStateSync() == 4116 || this.getQueuedBuffersSync() <= 0) {
                onEmpty.run();
            }
        });
    }

    protected void removeProcessedBuffersSync() {
        int processed = AL11.alGetSourcei(this.source, 4118);
        SoundManager.checkAlError();
        for (int i = 0; i < processed; ++i) {
            AL11.alSourceUnqueueBuffers(this.source);
            SoundManager.checkAlError();
        }
    }

    protected int getStateSync() {
        int state = AL11.alGetSourcei(this.source, 4112);
        SoundManager.checkAlError();
        return state;
    }

    protected int getQueuedBuffersSync() {
        int buffers = AL11.alGetSourcei(this.source, 4117);
        SoundManager.checkAlError();
        return buffers;
    }

    protected boolean hasValidSourceSync() {
        boolean validSource = AL11.alIsSource(this.source);
        SoundManager.checkAlError();
        return validSource;
    }

    public void runInContext(Runnable runnable) {
        if (this.executor.isShutdown()) {
            return;
        }
        this.soundManager.runInContext(this.executor, runnable);
    }

    public void fetchQueuedBuffersAsync(Consumer<Integer> supplier) {
        this.runInContext(() -> {
            if (this.isStoppedSync()) {
                supplier.accept(-1);
                return;
            }
            supplier.accept(this.getQueuedBuffersSync());
        });
    }
}
