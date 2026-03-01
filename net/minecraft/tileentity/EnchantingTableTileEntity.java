package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.INameable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchantingTableTileEntity
extends TileEntity
implements INameable,
ITickableTileEntity {
    public int ticks;
    public float field_195523_f;
    public float field_195524_g;
    public float field_195525_h;
    public float field_195526_i;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float nextPageAngle;
    public float pageAngle;
    public float field_195531_n;
    private static final Random random = new Random();
    private ITextComponent customname;

    public EnchantingTableTileEntity() {
        super(TileEntityType.ENCHANTING_TABLE);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (this.hasCustomName()) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customname));
        }
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains("CustomName", 8)) {
            this.customname = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
        }
    }

    @Override
    public void tick() {
        float f2;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.pageAngle = this.nextPageAngle;
        PlayerEntity playerentity = this.world.getClosestPlayer((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5, 3.0, false);
        if (playerentity != null) {
            double d0 = playerentity.getPosX() - ((double)this.pos.getX() + 0.5);
            double d1 = playerentity.getPosZ() - ((double)this.pos.getZ() + 0.5);
            this.field_195531_n = (float)MathHelper.atan2(d1, d0);
            this.nextPageTurningSpeed += 0.1f;
            if (this.nextPageTurningSpeed < 0.5f || random.nextInt(40) == 0) {
                float f1 = this.field_195525_h;
                do {
                    this.field_195525_h += (float)(random.nextInt(4) - random.nextInt(4));
                } while (f1 == this.field_195525_h);
            }
        } else {
            this.field_195531_n += 0.02f;
            this.nextPageTurningSpeed -= 0.1f;
        }
        while (this.nextPageAngle >= (float)Math.PI) {
            this.nextPageAngle -= (float)Math.PI * 2;
        }
        while (this.nextPageAngle < (float)(-Math.PI)) {
            this.nextPageAngle += (float)Math.PI * 2;
        }
        while (this.field_195531_n >= (float)Math.PI) {
            this.field_195531_n -= (float)Math.PI * 2;
        }
        while (this.field_195531_n < (float)(-Math.PI)) {
            this.field_195531_n += (float)Math.PI * 2;
        }
        for (f2 = this.field_195531_n - this.nextPageAngle; f2 >= (float)Math.PI; f2 -= (float)Math.PI * 2) {
        }
        while (f2 < (float)(-Math.PI)) {
            f2 += (float)Math.PI * 2;
        }
        this.nextPageAngle += f2 * 0.4f;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        ++this.ticks;
        this.field_195524_g = this.field_195523_f;
        float f = (this.field_195525_h - this.field_195523_f) * 0.4f;
        float f3 = 0.2f;
        f = MathHelper.clamp(f, -0.2f, 0.2f);
        this.field_195526_i += (f - this.field_195526_i) * 0.9f;
        this.field_195523_f += this.field_195526_i;
    }

    @Override
    public ITextComponent getName() {
        return this.customname != null ? this.customname : new TranslationTextComponent("container.enchant");
    }

    public void setCustomName(@Nullable ITextComponent name) {
        this.customname = name;
    }

    @Override
    @Nullable
    public ITextComponent getCustomName() {
        return this.customname;
    }
}
