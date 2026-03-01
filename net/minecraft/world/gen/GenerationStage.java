package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.IStringSerializable;

public class GenerationStage {

    public static enum Decoration {
        RAW_GENERATION,
        LAKES,
        LOCAL_MODIFICATIONS,
        UNDERGROUND_STRUCTURES,
        SURFACE_STRUCTURES,
        STRONGHOLDS,
        UNDERGROUND_ORES,
        UNDERGROUND_DECORATION,
        VEGETAL_DECORATION,
        TOP_LAYER_MODIFICATION;

    }

    public static enum Carving implements IStringSerializable
    {
        AIR("air"),
        LIQUID("liquid");

        public static final Codec<Carving> field_236074_c_;
        private static final Map<String, Carving> BY_NAME;
        private final String name;

        private Carving(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Carving func_236075_a_(String p_236075_0_) {
            return BY_NAME.get(p_236075_0_);
        }

        @Override
        public String getString() {
            return this.name;
        }

        static {
            field_236074_c_ = IStringSerializable.createEnumCodec(Carving::values, Carving::func_236075_a_);
            BY_NAME = Arrays.stream(Carving.values()).collect(Collectors.toMap(Carving::getName, p_222672_0_ -> p_222672_0_));
        }
    }
}
