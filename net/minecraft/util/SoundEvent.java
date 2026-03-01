package net.minecraft.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;

public class SoundEvent {
    public static final Codec<SoundEvent> CODEC = ResourceLocation.CODEC.xmap(SoundEvent::new, sound -> sound.name);
    private final ResourceLocation name;

    public SoundEvent(ResourceLocation name) {
        this.name = name;
    }

    public ResourceLocation getName() {
        return this.name;
    }
}
