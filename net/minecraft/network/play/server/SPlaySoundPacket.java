package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;

public class SPlaySoundPacket
implements IPacket<IClientPlayNetHandler> {
    private ResourceLocation soundName;
    private SoundCategory category;
    private int x;
    private int y = Integer.MAX_VALUE;
    private int z;
    private float volume;
    private float pitch;

    public SPlaySoundPacket() {
    }

    public SPlaySoundPacket(ResourceLocation p_i47939_1_, SoundCategory p_i47939_2_, Vector3d p_i47939_3_, float p_i47939_4_, float p_i47939_5_) {
        this.soundName = p_i47939_1_;
        this.category = p_i47939_2_;
        this.x = (int)(p_i47939_3_.x * 8.0);
        this.y = (int)(p_i47939_3_.y * 8.0);
        this.z = (int)(p_i47939_3_.z * 8.0);
        this.volume = p_i47939_4_;
        this.pitch = p_i47939_5_;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.soundName = buf.readResourceLocation();
        this.category = buf.readEnumValue(SoundCategory.class);
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeResourceLocation(this.soundName);
        buf.writeEnumValue(this.category);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }

    public ResourceLocation getSoundName() {
        return this.soundName;
    }

    public SoundCategory getCategory() {
        return this.category;
    }

    public double getX() {
        return (float)this.x / 8.0f;
    }

    public double getY() {
        return (float)this.y / 8.0f;
    }

    public double getZ() {
        return (float)this.z / 8.0f;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleCustomSound(this);
    }
}
