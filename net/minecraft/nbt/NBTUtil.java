package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NBTUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static GameProfile readGameProfile(CompoundNBT compound) {
        String s = null;
        UUID uuid = null;
        if (compound.contains("Name", 8)) {
            s = compound.getString("Name");
        }
        if (compound.hasUniqueId("Id")) {
            uuid = compound.getUniqueId("Id");
        }
        try {
            GameProfile gameprofile = new GameProfile(uuid, s);
            if (compound.contains("Properties", 10)) {
                CompoundNBT compoundnbt = compound.getCompound("Properties");
                for (String s1 : compoundnbt.keySet()) {
                    ListNBT listnbt = compoundnbt.getList(s1, 10);
                    for (int i = 0; i < listnbt.size(); ++i) {
                        CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                        String s2 = compoundnbt1.getString("Value");
                        if (compoundnbt1.contains("Signature", 8)) {
                            gameprofile.getProperties().put(s1, new Property(s1, s2, compoundnbt1.getString("Signature")));
                            continue;
                        }
                        gameprofile.getProperties().put(s1, new Property(s1, s2));
                    }
                }
            }
            return gameprofile;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public static CompoundNBT writeGameProfile(CompoundNBT tagCompound, GameProfile profile) {
        if (!StringUtils.isNullOrEmpty(profile.getName())) {
            tagCompound.putString("Name", profile.getName());
        }
        if (profile.getId() != null) {
            tagCompound.putUniqueId("Id", profile.getId());
        }
        if (!profile.getProperties().isEmpty()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            for (String s : profile.getProperties().keySet()) {
                ListNBT listnbt = new ListNBT();
                for (Property property : profile.getProperties().get(s)) {
                    CompoundNBT compoundnbt1 = new CompoundNBT();
                    compoundnbt1.putString("Value", property.getValue());
                    if (property.hasSignature()) {
                        compoundnbt1.putString("Signature", property.getSignature());
                    }
                    listnbt.add(compoundnbt1);
                }
                compoundnbt.put(s, listnbt);
            }
            tagCompound.put("Properties", compoundnbt);
        }
        return tagCompound;
    }

    @VisibleForTesting
    public static boolean areNBTEquals(@Nullable INBT nbt1, @Nullable INBT nbt2, boolean compareTagList) {
        if (nbt1 == nbt2) {
            return true;
        }
        if (nbt1 == null) {
            return true;
        }
        if (nbt2 == null) {
            return false;
        }
        if (!nbt1.getClass().equals(nbt2.getClass())) {
            return false;
        }
        if (nbt1 instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)nbt1;
            CompoundNBT compoundnbt1 = (CompoundNBT)nbt2;
            for (String s : compoundnbt.keySet()) {
                INBT inbt1 = compoundnbt.get(s);
                if (NBTUtil.areNBTEquals(inbt1, compoundnbt1.get(s), compareTagList)) continue;
                return false;
            }
            return true;
        }
        if (nbt1 instanceof ListNBT && compareTagList) {
            ListNBT listnbt = (ListNBT)nbt1;
            ListNBT listnbt1 = (ListNBT)nbt2;
            if (listnbt.isEmpty()) {
                return listnbt1.isEmpty();
            }
            for (int i = 0; i < listnbt.size(); ++i) {
                INBT inbt = listnbt.get(i);
                boolean flag = false;
                for (int j = 0; j < listnbt1.size(); ++j) {
                    if (!NBTUtil.areNBTEquals(inbt, listnbt1.get(j), compareTagList)) continue;
                    flag = true;
                    break;
                }
                if (flag) continue;
                return false;
            }
            return true;
        }
        return nbt1.equals(nbt2);
    }

    public static IntArrayNBT func_240626_a_(UUID p_240626_0_) {
        return new IntArrayNBT(UUIDCodec.encodeUUID(p_240626_0_));
    }

    public static UUID readUniqueId(INBT tag) {
        if (tag.getType() != IntArrayNBT.TYPE) {
            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + IntArrayNBT.TYPE.getName() + ", but found " + tag.getType().getName() + ".");
        }
        int[] aint = ((IntArrayNBT)tag).getIntArray();
        if (aint.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + aint.length + ".");
        }
        return UUIDCodec.decodeUUID(aint);
    }

    public static BlockPos readBlockPos(CompoundNBT tag) {
        return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
    }

    public static CompoundNBT writeBlockPos(BlockPos pos) {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putInt("X", pos.getX());
        compoundnbt.putInt("Y", pos.getY());
        compoundnbt.putInt("Z", pos.getZ());
        return compoundnbt;
    }

    public static BlockState readBlockState(CompoundNBT tag) {
        if (!tag.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        Block block = Registry.BLOCK.getOrDefault(new ResourceLocation(tag.getString("Name")));
        BlockState blockstate = block.getDefaultState();
        if (tag.contains("Properties", 10)) {
            CompoundNBT compoundnbt = tag.getCompound("Properties");
            StateContainer<Block, BlockState> statecontainer = block.getStateContainer();
            for (String s : compoundnbt.keySet()) {
                net.minecraft.state.Property<?> property = statecontainer.getProperty(s);
                if (property == null) continue;
                blockstate = NBTUtil.setValueHelper(blockstate, property, s, compoundnbt, tag);
            }
        }
        return blockstate;
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S p_193590_0_, net.minecraft.state.Property<T> p_193590_1_, String p_193590_2_, CompoundNBT p_193590_3_, CompoundNBT p_193590_4_) {
        Optional<T> optional = p_193590_1_.parseValue(p_193590_3_.getString(p_193590_2_));
        if (optional.isPresent()) {
            return (S)((StateHolder)p_193590_0_.with(p_193590_1_, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", (Object)p_193590_2_, (Object)p_193590_3_.getString(p_193590_2_), (Object)p_193590_4_.toString());
        return p_193590_0_;
    }

    public static CompoundNBT writeBlockState(BlockState tag) {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("Name", Registry.BLOCK.getKey(tag.getBlock()).toString());
        ImmutableMap<net.minecraft.state.Property<?>, Comparable<?>> immutablemap = tag.getValues();
        if (!immutablemap.isEmpty()) {
            CompoundNBT compoundnbt1 = new CompoundNBT();
            for (Map.Entry entry : immutablemap.entrySet()) {
                net.minecraft.state.Property property = (net.minecraft.state.Property)entry.getKey();
                compoundnbt1.putString(property.getName(), NBTUtil.getName(property, (Comparable)entry.getValue()));
            }
            compoundnbt.put("Properties", compoundnbt1);
        }
        return compoundnbt;
    }

    private static <T extends Comparable<T>> String getName(net.minecraft.state.Property<T> p_190010_0_, Comparable<?> p_190010_1_) {
        return p_190010_0_.getName(p_190010_1_);
    }

    public static CompoundNBT update(DataFixer dataFixer, DefaultTypeReferences type, CompoundNBT nbt, int version) {
        return NBTUtil.update(dataFixer, type, nbt, version, SharedConstants.getVersion().getWorldVersion());
    }

    public static CompoundNBT update(DataFixer dataFixer, DefaultTypeReferences type, CompoundNBT nbt, int version, int newVersion) {
        return dataFixer.update(type.getTypeReference(), new Dynamic<CompoundNBT>(NBTDynamicOps.INSTANCE, nbt), version, newVersion).getValue();
    }
}
