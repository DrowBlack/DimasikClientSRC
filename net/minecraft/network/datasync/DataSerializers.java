package net.minecraft.network.datasync;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
    private static final IntIdentityHashBiMap<IDataSerializer<?>> REGISTRY = new IntIdentityHashBiMap(16);
    public static final IDataSerializer<Byte> BYTE = new IDataSerializer<Byte>(){

        @Override
        public void write(PacketBuffer buf, Byte value) {
            buf.writeByte(value.byteValue());
        }

        @Override
        public Byte read(PacketBuffer buf) {
            return buf.readByte();
        }

        @Override
        public Byte copyValue(Byte value) {
            return value;
        }
    };
    public static final IDataSerializer<Integer> VARINT = new IDataSerializer<Integer>(){

        @Override
        public void write(PacketBuffer buf, Integer value) {
            buf.writeVarInt(value);
        }

        @Override
        public Integer read(PacketBuffer buf) {
            return buf.readVarInt();
        }

        @Override
        public Integer copyValue(Integer value) {
            return value;
        }
    };
    public static final IDataSerializer<Float> FLOAT = new IDataSerializer<Float>(){

        @Override
        public void write(PacketBuffer buf, Float value) {
            buf.writeFloat(value.floatValue());
        }

        @Override
        public Float read(PacketBuffer buf) {
            return Float.valueOf(buf.readFloat());
        }

        @Override
        public Float copyValue(Float value) {
            return value;
        }
    };
    public static final IDataSerializer<String> STRING = new IDataSerializer<String>(){

        @Override
        public void write(PacketBuffer buf, String value) {
            buf.writeString(value);
        }

        @Override
        public String read(PacketBuffer buf) {
            return buf.readString(Short.MAX_VALUE);
        }

        @Override
        public String copyValue(String value) {
            return value;
        }
    };
    public static final IDataSerializer<ITextComponent> TEXT_COMPONENT = new IDataSerializer<ITextComponent>(){

        @Override
        public void write(PacketBuffer buf, ITextComponent value) {
            buf.writeTextComponent(value);
        }

        @Override
        public ITextComponent read(PacketBuffer buf) {
            return buf.readTextComponent();
        }

        @Override
        public ITextComponent copyValue(ITextComponent value) {
            return value;
        }
    };
    public static final IDataSerializer<Optional<ITextComponent>> OPTIONAL_TEXT_COMPONENT = new IDataSerializer<Optional<ITextComponent>>(){

        @Override
        public void write(PacketBuffer buf, Optional<ITextComponent> value) {
            if (value.isPresent()) {
                buf.writeBoolean(true);
                buf.writeTextComponent(value.get());
            } else {
                buf.writeBoolean(false);
            }
        }

        @Override
        public Optional<ITextComponent> read(PacketBuffer buf) {
            return buf.readBoolean() ? Optional.of(buf.readTextComponent()) : Optional.empty();
        }

        @Override
        public Optional<ITextComponent> copyValue(Optional<ITextComponent> value) {
            return value;
        }
    };
    public static final IDataSerializer<ItemStack> ITEMSTACK = new IDataSerializer<ItemStack>(){

        @Override
        public void write(PacketBuffer buf, ItemStack value) {
            buf.writeItemStack(value);
        }

        @Override
        public ItemStack read(PacketBuffer buf) {
            return buf.readItemStack();
        }

        @Override
        public ItemStack copyValue(ItemStack value) {
            return value.copy();
        }
    };
    public static final IDataSerializer<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new IDataSerializer<Optional<BlockState>>(){

        @Override
        public void write(PacketBuffer buf, Optional<BlockState> value) {
            if (value.isPresent()) {
                buf.writeVarInt(Block.getStateId(value.get()));
            } else {
                buf.writeVarInt(0);
            }
        }

        @Override
        public Optional<BlockState> read(PacketBuffer buf) {
            int i = buf.readVarInt();
            return i == 0 ? Optional.empty() : Optional.of(Block.getStateById(i));
        }

        @Override
        public Optional<BlockState> copyValue(Optional<BlockState> value) {
            return value;
        }
    };
    public static final IDataSerializer<Boolean> BOOLEAN = new IDataSerializer<Boolean>(){

        @Override
        public void write(PacketBuffer buf, Boolean value) {
            buf.writeBoolean(value);
        }

        @Override
        public Boolean read(PacketBuffer buf) {
            return buf.readBoolean();
        }

        @Override
        public Boolean copyValue(Boolean value) {
            return value;
        }
    };
    public static final IDataSerializer<IParticleData> PARTICLE_DATA = new IDataSerializer<IParticleData>(){

        @Override
        public void write(PacketBuffer buf, IParticleData value) {
            buf.writeVarInt(Registry.PARTICLE_TYPE.getId(value.getType()));
            value.write(buf);
        }

        @Override
        public IParticleData read(PacketBuffer buf) {
            return this.read(buf, (ParticleType)Registry.PARTICLE_TYPE.getByValue(buf.readVarInt()));
        }

        private <T extends IParticleData> T read(PacketBuffer p_200543_1_, ParticleType<T> p_200543_2_) {
            return p_200543_2_.getDeserializer().read(p_200543_2_, p_200543_1_);
        }

        @Override
        public IParticleData copyValue(IParticleData value) {
            return value;
        }
    };
    public static final IDataSerializer<Rotations> ROTATIONS = new IDataSerializer<Rotations>(){

        @Override
        public void write(PacketBuffer buf, Rotations value) {
            buf.writeFloat(value.getX());
            buf.writeFloat(value.getY());
            buf.writeFloat(value.getZ());
        }

        @Override
        public Rotations read(PacketBuffer buf) {
            return new Rotations(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public Rotations copyValue(Rotations value) {
            return value;
        }
    };
    public static final IDataSerializer<BlockPos> BLOCK_POS = new IDataSerializer<BlockPos>(){

        @Override
        public void write(PacketBuffer buf, BlockPos value) {
            buf.writeBlockPos(value);
        }

        @Override
        public BlockPos read(PacketBuffer buf) {
            return buf.readBlockPos();
        }

        @Override
        public BlockPos copyValue(BlockPos value) {
            return value;
        }
    };
    public static final IDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new IDataSerializer<Optional<BlockPos>>(){

        @Override
        public void write(PacketBuffer buf, Optional<BlockPos> value) {
            buf.writeBoolean(value.isPresent());
            if (value.isPresent()) {
                buf.writeBlockPos(value.get());
            }
        }

        @Override
        public Optional<BlockPos> read(PacketBuffer buf) {
            return !buf.readBoolean() ? Optional.empty() : Optional.of(buf.readBlockPos());
        }

        @Override
        public Optional<BlockPos> copyValue(Optional<BlockPos> value) {
            return value;
        }
    };
    public static final IDataSerializer<Direction> DIRECTION = new IDataSerializer<Direction>(){

        @Override
        public void write(PacketBuffer buf, Direction value) {
            buf.writeEnumValue(value);
        }

        @Override
        public Direction read(PacketBuffer buf) {
            return buf.readEnumValue(Direction.class);
        }

        @Override
        public Direction copyValue(Direction value) {
            return value;
        }
    };
    public static final IDataSerializer<Optional<UUID>> OPTIONAL_UNIQUE_ID = new IDataSerializer<Optional<UUID>>(){

        @Override
        public void write(PacketBuffer buf, Optional<UUID> value) {
            buf.writeBoolean(value.isPresent());
            if (value.isPresent()) {
                buf.writeUniqueId(value.get());
            }
        }

        @Override
        public Optional<UUID> read(PacketBuffer buf) {
            return !buf.readBoolean() ? Optional.empty() : Optional.of(buf.readUniqueId());
        }

        @Override
        public Optional<UUID> copyValue(Optional<UUID> value) {
            return value;
        }
    };
    public static final IDataSerializer<CompoundNBT> COMPOUND_NBT = new IDataSerializer<CompoundNBT>(){

        @Override
        public void write(PacketBuffer buf, CompoundNBT value) {
            buf.writeCompoundTag(value);
        }

        @Override
        public CompoundNBT read(PacketBuffer buf) {
            return buf.readCompoundTag();
        }

        @Override
        public CompoundNBT copyValue(CompoundNBT value) {
            return value.copy();
        }
    };
    public static final IDataSerializer<VillagerData> VILLAGER_DATA = new IDataSerializer<VillagerData>(){

        @Override
        public void write(PacketBuffer buf, VillagerData value) {
            buf.writeVarInt(Registry.VILLAGER_TYPE.getId(value.getType()));
            buf.writeVarInt(Registry.VILLAGER_PROFESSION.getId(value.getProfession()));
            buf.writeVarInt(value.getLevel());
        }

        @Override
        public VillagerData read(PacketBuffer buf) {
            return new VillagerData(Registry.VILLAGER_TYPE.getByValue(buf.readVarInt()), Registry.VILLAGER_PROFESSION.getByValue(buf.readVarInt()), buf.readVarInt());
        }

        @Override
        public VillagerData copyValue(VillagerData value) {
            return value;
        }
    };
    public static final IDataSerializer<OptionalInt> OPTIONAL_VARINT = new IDataSerializer<OptionalInt>(){

        @Override
        public void write(PacketBuffer buf, OptionalInt value) {
            buf.writeVarInt(value.orElse(-1) + 1);
        }

        @Override
        public OptionalInt read(PacketBuffer buf) {
            int i = buf.readVarInt();
            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        @Override
        public OptionalInt copyValue(OptionalInt value) {
            return value;
        }
    };
    public static final IDataSerializer<Pose> POSE = new IDataSerializer<Pose>(){

        @Override
        public void write(PacketBuffer buf, Pose value) {
            buf.writeEnumValue(value);
        }

        @Override
        public Pose read(PacketBuffer buf) {
            return buf.readEnumValue(Pose.class);
        }

        @Override
        public Pose copyValue(Pose value) {
            return value;
        }
    };

    public static void registerSerializer(IDataSerializer<?> serializer) {
        REGISTRY.add(serializer);
    }

    @Nullable
    public static IDataSerializer<?> getSerializer(int id) {
        return REGISTRY.getByValue(id);
    }

    public static int getSerializerId(IDataSerializer<?> serializer) {
        return REGISTRY.getId(serializer);
    }

    static {
        DataSerializers.registerSerializer(BYTE);
        DataSerializers.registerSerializer(VARINT);
        DataSerializers.registerSerializer(FLOAT);
        DataSerializers.registerSerializer(STRING);
        DataSerializers.registerSerializer(TEXT_COMPONENT);
        DataSerializers.registerSerializer(OPTIONAL_TEXT_COMPONENT);
        DataSerializers.registerSerializer(ITEMSTACK);
        DataSerializers.registerSerializer(BOOLEAN);
        DataSerializers.registerSerializer(ROTATIONS);
        DataSerializers.registerSerializer(BLOCK_POS);
        DataSerializers.registerSerializer(OPTIONAL_BLOCK_POS);
        DataSerializers.registerSerializer(DIRECTION);
        DataSerializers.registerSerializer(OPTIONAL_UNIQUE_ID);
        DataSerializers.registerSerializer(OPTIONAL_BLOCK_STATE);
        DataSerializers.registerSerializer(COMPOUND_NBT);
        DataSerializers.registerSerializer(PARTICLE_DATA);
        DataSerializers.registerSerializer(VILLAGER_DATA);
        DataSerializers.registerSerializer(OPTIONAL_VARINT);
        DataSerializers.registerSerializer(POSE);
    }
}
