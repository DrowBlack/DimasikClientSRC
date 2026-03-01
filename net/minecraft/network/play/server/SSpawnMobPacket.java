package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;

public class SSpawnMobPacket
implements IPacket<IClientPlayNetHandler> {
    private int entityId;
    private UUID uniqueId;
    private int type;
    private double x;
    private double y;
    private double z;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private byte yaw;
    private byte pitch;
    private byte headPitch;

    public SSpawnMobPacket() {
    }

    public SSpawnMobPacket(LivingEntity entityIn) {
        this.entityId = entityIn.getEntityId();
        this.uniqueId = entityIn.getUniqueID();
        this.type = Registry.ENTITY_TYPE.getId(entityIn.getType());
        this.x = entityIn.getPosX();
        this.y = entityIn.getPosY();
        this.z = entityIn.getPosZ();
        this.yaw = (byte)(entityIn.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(entityIn.rotationPitch * 256.0f / 360.0f);
        this.headPitch = (byte)(entityIn.rotationYawHead * 256.0f / 360.0f);
        double d0 = 3.9;
        Vector3d vector3d = entityIn.getMotion();
        double d1 = MathHelper.clamp(vector3d.x, -3.9, 3.9);
        double d2 = MathHelper.clamp(vector3d.y, -3.9, 3.9);
        double d3 = MathHelper.clamp(vector3d.z, -3.9, 3.9);
        this.velocityX = (int)(d1 * 8000.0);
        this.velocityY = (int)(d2 * 8000.0);
        this.velocityZ = (int)(d3 * 8000.0);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.uniqueId = buf.readUniqueId();
        this.type = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.headPitch = buf.readByte();
        this.velocityX = buf.readShort();
        this.velocityY = buf.readShort();
        this.velocityZ = buf.readShort();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeUniqueId(this.uniqueId);
        buf.writeVarInt(this.type);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeByte(this.headPitch);
        buf.writeShort(this.velocityX);
        buf.writeShort(this.velocityY);
        buf.writeShort(this.velocityZ);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleSpawnMob(this);
    }

    public int getEntityID() {
        return this.entityId;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public int getEntityType() {
        return this.type;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getVelocityX() {
        return this.velocityX;
    }

    public int getVelocityY() {
        return this.velocityY;
    }

    public int getVelocityZ() {
        return this.velocityZ;
    }

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public byte getHeadPitch() {
        return this.headPitch;
    }
}
