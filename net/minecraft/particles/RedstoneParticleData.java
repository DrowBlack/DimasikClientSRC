package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class RedstoneParticleData
implements IParticleData {
    public static final RedstoneParticleData REDSTONE_DUST = new RedstoneParticleData(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Codec<RedstoneParticleData> field_239802_b_ = RecordCodecBuilder.create(p_239803_0_ -> p_239803_0_.group(((MapCodec)Codec.FLOAT.fieldOf("r")).forGetter(p_239807_0_ -> Float.valueOf(p_239807_0_.red)), ((MapCodec)Codec.FLOAT.fieldOf("g")).forGetter(p_239806_0_ -> Float.valueOf(p_239806_0_.green)), ((MapCodec)Codec.FLOAT.fieldOf("b")).forGetter(p_239805_0_ -> Float.valueOf(p_239805_0_.blue)), ((MapCodec)Codec.FLOAT.fieldOf("scale")).forGetter(p_239804_0_ -> Float.valueOf(p_239804_0_.alpha))).apply((Applicative<RedstoneParticleData, ?>)p_239803_0_, RedstoneParticleData::new));
    public static final IParticleData.IDeserializer<RedstoneParticleData> DESERIALIZER = new IParticleData.IDeserializer<RedstoneParticleData>(){

        @Override
        public RedstoneParticleData deserialize(ParticleType<RedstoneParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float f = (float)reader.readDouble();
            reader.expect(' ');
            float f1 = (float)reader.readDouble();
            reader.expect(' ');
            float f2 = (float)reader.readDouble();
            reader.expect(' ');
            float f3 = (float)reader.readDouble();
            return new RedstoneParticleData(f, f1, f2, f3);
        }

        @Override
        public RedstoneParticleData read(ParticleType<RedstoneParticleData> particleTypeIn, PacketBuffer buffer) {
            return new RedstoneParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public RedstoneParticleData(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = MathHelper.clamp(alpha, 0.01f, 4.0f);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.alpha);
    }

    @Override
    public String getParameters() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), Float.valueOf(this.red), Float.valueOf(this.green), Float.valueOf(this.blue), Float.valueOf(this.alpha));
    }

    public ParticleType<RedstoneParticleData> getType() {
        return ParticleTypes.DUST;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getAlpha() {
        return this.alpha;
    }
}
