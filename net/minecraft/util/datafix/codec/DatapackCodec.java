package net.minecraft.util.datafix.codec;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DatapackCodec {
    public static final DatapackCodec VANILLA_CODEC = new DatapackCodec(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<DatapackCodec> CODEC = RecordCodecBuilder.create(builder -> builder.group(((MapCodec)Codec.STRING.listOf().fieldOf("Enabled")).forGetter(datapackCodec -> datapackCodec.enabled), ((MapCodec)Codec.STRING.listOf().fieldOf("Disabled")).forGetter(datapackCodec -> datapackCodec.disabled)).apply((Applicative<DatapackCodec, ?>)builder, DatapackCodec::new));
    private final List<String> enabled;
    private final List<String> disabled;

    public DatapackCodec(List<String> enabled, List<String> disabled) {
        this.enabled = ImmutableList.copyOf(enabled);
        this.disabled = ImmutableList.copyOf(disabled);
    }

    public List<String> getEnabled() {
        return this.enabled;
    }

    public List<String> getDisabled() {
        return this.disabled;
    }
}
