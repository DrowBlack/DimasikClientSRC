package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.StringUtils;

public class SkullTileEntity
extends TileEntity
implements ITickableTileEntity {
    @Nullable
    private static PlayerProfileCache profileCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private GameProfile playerProfile;
    private int dragonAnimatedTicks;
    private boolean dragonAnimated;

    public SkullTileEntity() {
        super(TileEntityType.SKULL);
    }

    public static void setProfileCache(PlayerProfileCache profileCacheIn) {
        profileCache = profileCacheIn;
    }

    public static void setSessionService(MinecraftSessionService sessionServiceIn) {
        sessionService = sessionServiceIn;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (this.playerProfile != null) {
            CompoundNBT compoundnbt = new CompoundNBT();
            NBTUtil.writeGameProfile(compoundnbt, this.playerProfile);
            compound.put("SkullOwner", compoundnbt);
        }
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        String s;
        super.read(state, nbt);
        if (nbt.contains("SkullOwner", 10)) {
            this.setPlayerProfile(NBTUtil.readGameProfile(nbt.getCompound("SkullOwner")));
        } else if (nbt.contains("ExtraType", 8) && !StringUtils.isNullOrEmpty(s = nbt.getString("ExtraType"))) {
            this.setPlayerProfile(new GameProfile(null, s));
        }
    }

    @Override
    public void tick() {
        BlockState blockstate = this.getBlockState();
        if (blockstate.isIn(Blocks.DRAGON_HEAD) || blockstate.isIn(Blocks.DRAGON_WALL_HEAD)) {
            if (this.world.isBlockPowered(this.pos)) {
                this.dragonAnimated = true;
                ++this.dragonAnimatedTicks;
            } else {
                this.dragonAnimated = false;
            }
        }
    }

    public float getAnimationProgress(float p_184295_1_) {
        return this.dragonAnimated ? (float)this.dragonAnimatedTicks + p_184295_1_ : (float)this.dragonAnimatedTicks;
    }

    @Nullable
    public GameProfile getPlayerProfile() {
        return this.playerProfile;
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 4, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public void setPlayerProfile(@Nullable GameProfile p_195485_1_) {
        this.playerProfile = p_195485_1_;
        this.updatePlayerProfile();
    }

    private void updatePlayerProfile() {
        this.playerProfile = SkullTileEntity.updateGameProfile(this.playerProfile);
        this.markDirty();
    }

    @Nullable
    public static GameProfile updateGameProfile(@Nullable GameProfile input) {
        if (input != null && !StringUtils.isNullOrEmpty(input.getName())) {
            if (input.isComplete() && input.getProperties().containsKey("textures")) {
                return input;
            }
            if (profileCache != null && sessionService != null) {
                GameProfile gameprofile = profileCache.getGameProfileForUsername(input.getName());
                if (gameprofile == null) {
                    return input;
                }
                Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), null);
                if (property == null) {
                    gameprofile = sessionService.fillProfileProperties(gameprofile, true);
                }
                return gameprofile;
            }
            return input;
        }
        return input;
    }
}
