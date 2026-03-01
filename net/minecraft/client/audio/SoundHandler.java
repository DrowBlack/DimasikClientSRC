package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler
extends ReloadListener<Loader> {
    public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0f, 1.0f, 1, Sound.Type.FILE, false, false, 16);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter((Type)((Object)SoundList.class), new SoundListSerializer()).create();
    private static final TypeToken<Map<String, SoundList>> TYPE = new TypeToken<Map<String, SoundList>>(){};
    private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();
    private final SoundEngine sndManager;

    public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn) {
        this.sndManager = new SoundEngine(this, gameSettingsIn, manager);
    }

    @Override
    protected Loader prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
        Loader soundhandler$loader = new Loader();
        profilerIn.startTick();
        for (String s : resourceManagerIn.getResourceNamespaces()) {
            profilerIn.startSection(s);
            try {
                for (IResource iresource : resourceManagerIn.getAllResources(new ResourceLocation(s, "sounds.json"))) {
                    profilerIn.startSection(iresource.getPackName());
                    try (InputStream inputstream = iresource.getInputStream();
                         InputStreamReader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);){
                        profilerIn.startSection("parse");
                        Map<String, SoundList> map = JSONUtils.fromJSONUnlenient(GSON, reader, TYPE);
                        profilerIn.endStartSection("register");
                        for (Map.Entry<String, SoundList> entry : map.entrySet()) {
                            soundhandler$loader.registerSoundEvent(new ResourceLocation(s, entry.getKey()), entry.getValue(), resourceManagerIn);
                        }
                        profilerIn.endSection();
                    }
                    catch (RuntimeException runtimeexception) {
                        LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", (Object)iresource.getPackName(), (Object)runtimeexception);
                    }
                    profilerIn.endSection();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            profilerIn.endSection();
        }
        profilerIn.endTick();
        return soundhandler$loader;
    }

    @Override
    protected void apply(Loader objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        objectIn.preloadSounds(this.soundRegistry, this.sndManager);
        for (ResourceLocation resourcelocation : this.soundRegistry.keySet()) {
            String s;
            SoundEventAccessor soundeventaccessor = this.soundRegistry.get(resourcelocation);
            if (!(soundeventaccessor.getSubtitle() instanceof TranslationTextComponent) || I18n.hasKey(s = ((TranslationTextComponent)soundeventaccessor.getSubtitle()).getKey())) continue;
            LOGGER.debug("Missing subtitle {} for event: {}", (Object)s, (Object)resourcelocation);
        }
        if (LOGGER.isDebugEnabled()) {
            for (ResourceLocation resourcelocation1 : this.soundRegistry.keySet()) {
                if (Registry.SOUND_EVENT.containsKey(resourcelocation1)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)resourcelocation1);
            }
        }
        this.sndManager.reload();
    }

    private static boolean isValidSound(Sound sound, ResourceLocation soundLocation, IResourceManager resourceManager) {
        ResourceLocation resourcelocation = sound.getSoundAsOggLocation();
        if (!resourceManager.hasResource(resourcelocation)) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)resourcelocation, (Object)soundLocation);
            return false;
        }
        return true;
    }

    @Nullable
    public SoundEventAccessor getAccessor(ResourceLocation location) {
        return this.soundRegistry.get(location);
    }

    public Collection<ResourceLocation> getAvailableSounds() {
        return this.soundRegistry.keySet();
    }

    public void playOnNextTick(ITickableSound tickableSound) {
        this.sndManager.playOnNextTick(tickableSound);
    }

    public void play(ISound sound) {
        this.sndManager.play(sound);
    }

    public void playDelayed(ISound sound, int delay) {
        this.sndManager.playDelayed(sound, delay);
    }

    public void updateListener(ActiveRenderInfo activeRenderInfo) {
        this.sndManager.updateListener(activeRenderInfo);
    }

    public void pause() {
        this.sndManager.pause();
    }

    public void stop() {
        this.sndManager.stopAllSounds();
    }

    public void unloadSounds() {
        this.sndManager.unload();
    }

    public void tick(boolean isGamePaused) {
        this.sndManager.tick(isGamePaused);
    }

    public void resume() {
        this.sndManager.resume();
    }

    public void setSoundLevel(SoundCategory category, float volume) {
        if (category == SoundCategory.MASTER && volume <= 0.0f) {
            this.stop();
        }
        this.sndManager.setVolume(category, volume);
    }

    public void stop(ISound soundIn) {
        this.sndManager.stop(soundIn);
    }

    public boolean isPlaying(ISound sound) {
        return this.sndManager.isPlaying(sound);
    }

    public void addListener(ISoundEventListener listener) {
        this.sndManager.addListener(listener);
    }

    public void removeListener(ISoundEventListener listener) {
        this.sndManager.removeListener(listener);
    }

    public void stop(@Nullable ResourceLocation id, @Nullable SoundCategory category) {
        this.sndManager.stop(id, category);
    }

    public String getDebugString() {
        return this.sndManager.getDebugString();
    }

    public static class Loader {
        private final Map<ResourceLocation, SoundEventAccessor> soundRegistry = Maps.newHashMap();

        protected Loader() {
        }

        private void registerSoundEvent(ResourceLocation soundLocation, SoundList soundList, IResourceManager resourceManager) {
            boolean flag;
            SoundEventAccessor soundeventaccessor = this.soundRegistry.get(soundLocation);
            boolean bl = flag = soundeventaccessor == null;
            if (flag || soundList.canReplaceExisting()) {
                if (!flag) {
                    LOGGER.debug("Replaced sound event location {}", (Object)soundLocation);
                }
                soundeventaccessor = new SoundEventAccessor(soundLocation, soundList.getSubtitle());
                this.soundRegistry.put(soundLocation, soundeventaccessor);
            }
            block4: for (final Sound sound : soundList.getSounds()) {
                final ResourceLocation resourcelocation = sound.getSoundLocation();
                soundeventaccessor.addSound(switch (sound.getType()) {
                    case Sound.Type.FILE -> {
                        if (!SoundHandler.isValidSound(sound, soundLocation, resourceManager)) continue block4;
                        yield sound;
                    }
                    case Sound.Type.SOUND_EVENT -> new ISoundEventAccessor<Sound>(){

                        @Override
                        public int getWeight() {
                            SoundEventAccessor soundeventaccessor1 = soundRegistry.get(resourcelocation);
                            return soundeventaccessor1 == null ? 0 : soundeventaccessor1.getWeight();
                        }

                        @Override
                        public Sound cloneEntry() {
                            SoundEventAccessor soundeventaccessor1 = soundRegistry.get(resourcelocation);
                            if (soundeventaccessor1 == null) {
                                return MISSING_SOUND;
                            }
                            Sound sound1 = soundeventaccessor1.cloneEntry();
                            return new Sound(sound1.getSoundLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.isStreaming() || sound.isStreaming(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                        }

                        @Override
                        public void enqueuePreload(SoundEngine engine) {
                            SoundEventAccessor soundeventaccessor1 = soundRegistry.get(resourcelocation);
                            if (soundeventaccessor1 != null) {
                                soundeventaccessor1.enqueuePreload(engine);
                            }
                        }
                    };
                    default -> throw new IllegalStateException("Unknown SoundEventRegistration type: " + String.valueOf((Object)sound.getType()));
                });
            }
        }

        public void preloadSounds(Map<ResourceLocation, SoundEventAccessor> soundRegistry, SoundEngine soundManager) {
            soundRegistry.clear();
            for (Map.Entry<ResourceLocation, SoundEventAccessor> entry : this.soundRegistry.entrySet()) {
                soundRegistry.put(entry.getKey(), entry.getValue());
                entry.getValue().enqueuePreload(soundManager);
            }
        }
    }
}
