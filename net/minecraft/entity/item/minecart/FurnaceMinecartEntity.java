package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FurnaceMinecartEntity
extends AbstractMinecartEntity {
    private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(FurnaceMinecartEntity.class, DataSerializers.BOOLEAN);
    private int fuel;
    public double pushX;
    public double pushZ;
    private static final Ingredient BURNABLE_FUELS = Ingredient.fromItems(Items.COAL, Items.CHARCOAL);

    public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> furnaceCart, World world) {
        super(furnaceCart, world);
    }

    public FurnaceMinecartEntity(World worldIn, double x, double y, double z) {
        super(EntityType.FURNACE_MINECART, worldIn, x, y, z);
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.FURNACE;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(POWERED, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote()) {
            if (this.fuel > 0) {
                --this.fuel;
            }
            if (this.fuel <= 0) {
                this.pushX = 0.0;
                this.pushZ = 0.0;
            }
            this.setMinecartPowered(this.fuel > 0);
        }
        if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
            this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getPosX(), this.getPosY() + 0.8, this.getPosZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected double getMaximumSpeed() {
        return 0.2;
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(Blocks.FURNACE);
        }
    }

    @Override
    protected void moveAlongTrack(BlockPos pos, BlockState state) {
        double d0 = 1.0E-4;
        double d1 = 0.001;
        super.moveAlongTrack(pos, state);
        Vector3d vector3d = this.getMotion();
        double d2 = FurnaceMinecartEntity.horizontalMag(vector3d);
        double d3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (d3 > 1.0E-4 && d2 > 0.001) {
            double d4 = MathHelper.sqrt(d2);
            double d5 = MathHelper.sqrt(d3);
            this.pushX = vector3d.x / d4 * d5;
            this.pushZ = vector3d.z / d4 * d5;
        }
    }

    @Override
    protected void applyDrag() {
        double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (d0 > 1.0E-7) {
            d0 = MathHelper.sqrt(d0);
            this.pushX /= d0;
            this.pushZ /= d0;
            this.setMotion(this.getMotion().mul(0.8, 0.0, 0.8).add(this.pushX, 0.0, this.pushZ));
        } else {
            this.setMotion(this.getMotion().mul(0.98, 0.0, 0.98));
        }
        super.applyDrag();
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (BURNABLE_FUELS.test(itemstack) && this.fuel + 3600 <= 32000) {
            if (!player.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            this.fuel += 3600;
        }
        if (this.fuel > 0) {
            this.pushX = this.getPosX() - player.getPosX();
            this.pushZ = this.getPosZ() - player.getPosZ();
        }
        return ActionResultType.func_233537_a_(this.world.isRemote);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putDouble("PushX", this.pushX);
        compound.putDouble("PushZ", this.pushZ);
        compound.putShort("Fuel", (short)this.fuel);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.pushX = compound.getDouble("PushX");
        this.pushZ = compound.getDouble("PushZ");
        this.fuel = compound.getShort("Fuel");
    }

    protected boolean isMinecartPowered() {
        return this.dataManager.get(POWERED);
    }

    protected void setMinecartPowered(boolean powered) {
        this.dataManager.set(POWERED, powered);
    }

    @Override
    public BlockState getDefaultDisplayTile() {
        return (BlockState)((BlockState)Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH)).with(FurnaceBlock.LIT, this.isMinecartPowered());
    }
}
