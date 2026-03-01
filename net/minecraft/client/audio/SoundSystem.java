package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.audio.ALUtils;
import net.minecraft.client.audio.Listener;
import net.minecraft.client.audio.SoundSource;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

public class SoundSystem {
    private static final Logger LOGGER = LogManager.getLogger();
    private long device;
    private long context;
    private static final IHandler DUMMY_HANDLER = new IHandler(){

        @Override
        @Nullable
        public SoundSource getSource() {
            return null;
        }

        @Override
        public boolean freeSource(SoundSource source) {
            return false;
        }

        @Override
        public void unload() {
        }

        @Override
        public int getMaxSoundSources() {
            return 0;
        }

        @Override
        public int getActiveSoundSourceCount() {
            return 0;
        }
    };
    private IHandler staticHandler = DUMMY_HANDLER;
    private IHandler streamingHandler = DUMMY_HANDLER;
    private final Listener listener = new Listener();

    public void init() {
        this.device = SoundSystem.openDevice();
        ALCCapabilities alccapabilities = ALC.createCapabilities(this.device);
        if (ALUtils.checkALCError(this.device, "Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        }
        if (!alccapabilities.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        }
        this.context = ALC10.alcCreateContext(this.device, (IntBuffer)null);
        ALC10.alcMakeContextCurrent(this.context);
        int i = this.getMaxChannels();
        int j = MathHelper.clamp((int)MathHelper.sqrt(i), 2, 8);
        int k = MathHelper.clamp(i - j, 8, 255);
        this.staticHandler = new HandlerImpl(k);
        this.streamingHandler = new HandlerImpl(j);
        ALCapabilities alcapabilities = AL.createCapabilities(alccapabilities);
        ALUtils.checkALError("Initialization");
        if (!alcapabilities.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
        }
        AL10.alEnable(512);
        if (!alcapabilities.AL_EXT_LINEAR_DISTANCE) {
            throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
        }
        ALUtils.checkALError("Enable per-source distance models");
        LOGGER.info("OpenAL initialized.");
    }

    private int getMaxChannels() {
        int i1;
        try (MemoryStack memorystack = MemoryStack.stackPush();){
            int l;
            int k;
            int i = ALC10.alcGetInteger(this.device, 4098);
            if (ALUtils.checkALCError(this.device, "Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            IntBuffer intbuffer = memorystack.mallocInt(i);
            ALC10.alcGetIntegerv(this.device, 4099, intbuffer);
            if (ALUtils.checkALCError(this.device, "Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            int j = 0;
            do {
                if (j >= i) {
                    int n = 30;
                    return n;
                }
                if ((k = intbuffer.get(j++)) == 0) {
                    int n = 30;
                    return n;
                }
                l = intbuffer.get(j++);
            } while (k != 4112);
            i1 = l;
        }
        return i1;
    }

    private static long openDevice() {
        for (int i = 0; i < 3; ++i) {
            long j = ALC10.alcOpenDevice((ByteBuffer)null);
            if (j == 0L || ALUtils.checkALCError(j, "Open device")) continue;
            return j;
        }
        throw new IllegalStateException("Failed to open OpenAL device");
    }

    public void unload() {
        this.staticHandler.unload();
        this.streamingHandler.unload();
        ALC10.alcDestroyContext(this.context);
        if (this.device != 0L) {
            ALC10.alcCloseDevice(this.device);
        }
    }

    public Listener getListener() {
        return this.listener;
    }

    @Nullable
    public SoundSource getSource(Mode soundMode) {
        return (soundMode == Mode.STREAMING ? this.streamingHandler : this.staticHandler).getSource();
    }

    public void release(SoundSource source) {
        if (!this.staticHandler.freeSource(source) && !this.streamingHandler.freeSource(source)) {
            throw new IllegalStateException("Tried to release unknown channel");
        }
    }

    public String getDebugString() {
        return String.format("Sounds: %d/%d + %d/%d", this.staticHandler.getActiveSoundSourceCount(), this.staticHandler.getMaxSoundSources(), this.streamingHandler.getActiveSoundSourceCount(), this.streamingHandler.getMaxSoundSources());
    }

    static interface IHandler {
        @Nullable
        public SoundSource getSource();

        public boolean freeSource(SoundSource var1);

        public void unload();

        public int getMaxSoundSources();

        public int getActiveSoundSourceCount();
    }

    static class HandlerImpl
    implements IHandler {
        private final int maxSoundSources;
        private final Set<SoundSource> activeSoundSources = Sets.newIdentityHashSet();

        public HandlerImpl(int maxSoundSources) {
            this.maxSoundSources = maxSoundSources;
        }

        @Override
        @Nullable
        public SoundSource getSource() {
            if (this.activeSoundSources.size() >= this.maxSoundSources) {
                LOGGER.warn("Maximum sound pool size {} reached", (Object)this.maxSoundSources);
                return null;
            }
            SoundSource soundsource = SoundSource.allocateNewSource();
            if (soundsource != null) {
                this.activeSoundSources.add(soundsource);
            }
            return soundsource;
        }

        @Override
        public boolean freeSource(SoundSource source) {
            if (!this.activeSoundSources.remove(source)) {
                return false;
            }
            source.close();
            return true;
        }

        @Override
        public void unload() {
            this.activeSoundSources.forEach(SoundSource::close);
            this.activeSoundSources.clear();
        }

        @Override
        public int getMaxSoundSources() {
            return this.maxSoundSources;
        }

        @Override
        public int getActiveSoundSourceCount() {
            return this.activeSoundSources.size();
        }
    }

    public static enum Mode {
        STATIC,
        STREAMING;

    }
}
