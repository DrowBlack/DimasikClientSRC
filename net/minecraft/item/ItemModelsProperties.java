package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemModelsProperties {
    private static final Map<ResourceLocation, IItemPropertyGetter> GLOBAL_PROPERTY_MAP = Maps.newHashMap();
    private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
    private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
    private static final IItemPropertyGetter field_239413_d_ = (p_239434_0_, p_239434_1_, p_239434_2_) -> p_239434_0_.isDamaged() ? 1.0f : 0.0f;
    private static final IItemPropertyGetter field_239414_e_ = (p_239433_0_, p_239433_1_, p_239433_2_) -> MathHelper.clamp((float)p_239433_0_.getDamage() / (float)p_239433_0_.getMaxDamage(), 0.0f, 1.0f);
    private static final Map<Item, Map<ResourceLocation, IItemPropertyGetter>> ITEM_PROPERTY_MAP = Maps.newHashMap();

    private static IItemPropertyGetter registerGlobalProperty(ResourceLocation id, IItemPropertyGetter propertyGetter) {
        GLOBAL_PROPERTY_MAP.put(id, propertyGetter);
        return propertyGetter;
    }

    private static void registerProperty(Item item, ResourceLocation p_239418_1_, IItemPropertyGetter p_239418_2_) {
        ITEM_PROPERTY_MAP.computeIfAbsent(item, p_239416_0_ -> Maps.newHashMap()).put(p_239418_1_, p_239418_2_);
    }

    @Nullable
    public static IItemPropertyGetter func_239417_a_(Item p_239417_0_, ResourceLocation p_239417_1_) {
        IItemPropertyGetter iitempropertygetter;
        if (p_239417_0_.getMaxDamage() > 0) {
            if (DAMAGE.equals(p_239417_1_)) {
                return field_239414_e_;
            }
            if (DAMAGED.equals(p_239417_1_)) {
                return field_239413_d_;
            }
        }
        if ((iitempropertygetter = GLOBAL_PROPERTY_MAP.get(p_239417_1_)) != null) {
            return iitempropertygetter;
        }
        Map<ResourceLocation, IItemPropertyGetter> map = ITEM_PROPERTY_MAP.get(p_239417_0_);
        return map == null ? null : map.get(p_239417_1_);
    }

    static {
        ItemModelsProperties.registerGlobalProperty(new ResourceLocation("lefthanded"), (p_239432_0_, p_239432_1_, p_239432_2_) -> p_239432_2_ != null && p_239432_2_.getPrimaryHand() != HandSide.RIGHT ? 1.0f : 0.0f);
        ItemModelsProperties.registerGlobalProperty(new ResourceLocation("cooldown"), (p_239431_0_, p_239431_1_, p_239431_2_) -> p_239431_2_ instanceof PlayerEntity ? ((PlayerEntity)p_239431_2_).getCooldownTracker().getCooldown(p_239431_0_.getItem(), 0.0f) : 0.0f);
        ItemModelsProperties.registerGlobalProperty(new ResourceLocation("custom_model_data"), (p_239430_0_, p_239430_1_, p_239430_2_) -> p_239430_0_.hasTag() ? (float)p_239430_0_.getTag().getInt("CustomModelData") : 0.0f);
        ItemModelsProperties.registerProperty(Items.BOW, new ResourceLocation("pull"), (p_239429_0_, p_239429_1_, p_239429_2_) -> {
            if (p_239429_2_ == null) {
                return 0.0f;
            }
            return p_239429_2_.getActiveItemStack() != p_239429_0_ ? 0.0f : (float)(p_239429_0_.getUseDuration() - p_239429_2_.getItemInUseCount()) / 20.0f;
        });
        ItemModelsProperties.registerProperty(Items.BOW, new ResourceLocation("pulling"), (p_239428_0_, p_239428_1_, p_239428_2_) -> p_239428_2_ != null && p_239428_2_.isHandActive() && p_239428_2_.getActiveItemStack() == p_239428_0_ ? 1.0f : 0.0f);
        ItemModelsProperties.registerProperty(Items.CLOCK, new ResourceLocation("time"), new IItemPropertyGetter(){
            private double field_239435_a_;
            private double field_239436_b_;
            private long field_239437_c_;

            @Override
            public float call(ItemStack p_call_1_, @Nullable ClientWorld p_call_2_, @Nullable LivingEntity p_call_3_) {
                Entity entity;
                Entity entity2 = entity = p_call_3_ != null ? p_call_3_ : p_call_1_.getAttachedEntity();
                if (entity == null) {
                    return 0.0f;
                }
                if (p_call_2_ == null && entity.world instanceof ClientWorld) {
                    p_call_2_ = (ClientWorld)entity.world;
                }
                if (p_call_2_ == null) {
                    return 0.0f;
                }
                double d0 = p_call_2_.getDimensionType().isNatural() ? (double)p_call_2_.func_242415_f(1.0f) : Math.random();
                d0 = this.func_239438_a_(p_call_2_, d0);
                return (float)d0;
            }

            private double func_239438_a_(World p_239438_1_, double p_239438_2_) {
                if (p_239438_1_.getGameTime() != this.field_239437_c_) {
                    this.field_239437_c_ = p_239438_1_.getGameTime();
                    double d0 = p_239438_2_ - this.field_239435_a_;
                    d0 = MathHelper.positiveModulo(d0 + 0.5, 1.0) - 0.5;
                    this.field_239436_b_ += d0 * 0.1;
                    this.field_239436_b_ *= 0.9;
                    this.field_239435_a_ = MathHelper.positiveModulo(this.field_239435_a_ + this.field_239436_b_, 1.0);
                }
                return this.field_239435_a_;
            }
        });
        ItemModelsProperties.registerProperty(Items.COMPASS, new ResourceLocation("angle"), new IItemPropertyGetter(){
            private final Angle field_239439_a_ = new Angle();
            private final Angle field_239440_b_ = new Angle();

            @Override
            public float call(ItemStack p_call_1_, @Nullable ClientWorld p_call_2_, @Nullable LivingEntity p_call_3_) {
                Entity entity;
                Entity entity2 = entity = p_call_3_ != null ? p_call_3_ : p_call_1_.getAttachedEntity();
                if (entity == null) {
                    return 0.0f;
                }
                if (p_call_2_ == null && entity.world instanceof ClientWorld) {
                    p_call_2_ = (ClientWorld)entity.world;
                }
                BlockPos blockpos = CompassItem.func_234670_d_(p_call_1_) ? this.func_239442_a_(p_call_2_, p_call_1_.getOrCreateTag()) : this.func_239444_a_(p_call_2_);
                long i = p_call_2_.getGameTime();
                if (blockpos != null && !(entity.getPositionVec().squareDistanceTo((double)blockpos.getX() + 0.5, entity.getPositionVec().getY(), (double)blockpos.getZ() + 0.5) < (double)1.0E-5f)) {
                    double d3;
                    boolean flag = p_call_3_ instanceof PlayerEntity && ((PlayerEntity)p_call_3_).isUser();
                    double d1 = 0.0;
                    if (flag) {
                        d1 = p_call_3_.rotationYaw;
                    } else if (entity instanceof ItemFrameEntity) {
                        d1 = this.func_239441_a_((ItemFrameEntity)entity);
                    } else if (entity instanceof ItemEntity) {
                        d1 = 180.0f - ((ItemEntity)entity).getItemHover(0.5f) / ((float)Math.PI * 2) * 360.0f;
                    } else if (p_call_3_ != null) {
                        d1 = p_call_3_.renderYawOffset;
                    }
                    d1 = MathHelper.positiveModulo(d1 / 360.0, 1.0);
                    double d2 = this.func_239443_a_(Vector3d.copyCentered(blockpos), entity) / 6.2831854820251465;
                    if (flag) {
                        if (this.field_239439_a_.func_239448_a_(i)) {
                            this.field_239439_a_.func_239449_a_(i, 0.5 - (d1 - 0.25));
                        }
                        d3 = d2 + this.field_239439_a_.field_239445_a_;
                    } else {
                        d3 = 0.5 - (d1 - 0.25 - d2);
                    }
                    return MathHelper.positiveModulo((float)d3, 1.0f);
                }
                if (this.field_239440_b_.func_239448_a_(i)) {
                    this.field_239440_b_.func_239449_a_(i, Math.random());
                }
                double d0 = this.field_239440_b_.field_239445_a_ + (double)((float)p_call_1_.hashCode() / 2.1474836E9f);
                return MathHelper.positiveModulo((float)d0, 1.0f);
            }

            @Nullable
            private BlockPos func_239444_a_(ClientWorld p_239444_1_) {
                return p_239444_1_.getDimensionType().isNatural() ? p_239444_1_.func_239140_u_() : null;
            }

            @Nullable
            private BlockPos func_239442_a_(World p_239442_1_, CompoundNBT p_239442_2_) {
                Optional<RegistryKey<World>> optional;
                boolean flag = p_239442_2_.contains("LodestonePos");
                boolean flag1 = p_239442_2_.contains("LodestoneDimension");
                if (flag && flag1 && (optional = CompassItem.func_234667_a_(p_239442_2_)).isPresent() && p_239442_1_.getDimensionKey() == optional.get()) {
                    return NBTUtil.readBlockPos(p_239442_2_.getCompound("LodestonePos"));
                }
                return null;
            }

            private double func_239441_a_(ItemFrameEntity p_239441_1_) {
                Direction direction = p_239441_1_.getHorizontalFacing();
                int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getOffset() : 0;
                return MathHelper.wrapDegrees(180 + direction.getHorizontalIndex() * 90 + p_239441_1_.getRotation() * 45 + i);
            }

            private double func_239443_a_(Vector3d p_239443_1_, Entity p_239443_2_) {
                return Math.atan2(p_239443_1_.getZ() - p_239443_2_.getPosZ(), p_239443_1_.getX() - p_239443_2_.getPosX());
            }
        });
        ItemModelsProperties.registerProperty(Items.CROSSBOW, new ResourceLocation("pull"), (p_239427_0_, p_239427_1_, p_239427_2_) -> {
            if (p_239427_2_ == null) {
                return 0.0f;
            }
            return CrossbowItem.isCharged(p_239427_0_) ? 0.0f : (float)(p_239427_0_.getUseDuration() - p_239427_2_.getItemInUseCount()) / (float)CrossbowItem.getChargeTime(p_239427_0_);
        });
        ItemModelsProperties.registerProperty(Items.CROSSBOW, new ResourceLocation("pulling"), (p_239426_0_, p_239426_1_, p_239426_2_) -> p_239426_2_ != null && p_239426_2_.isHandActive() && p_239426_2_.getActiveItemStack() == p_239426_0_ && !CrossbowItem.isCharged(p_239426_0_) ? 1.0f : 0.0f);
        ItemModelsProperties.registerProperty(Items.CROSSBOW, new ResourceLocation("charged"), (p_239425_0_, p_239425_1_, p_239425_2_) -> p_239425_2_ != null && CrossbowItem.isCharged(p_239425_0_) ? 1.0f : 0.0f);
        ItemModelsProperties.registerProperty(Items.CROSSBOW, new ResourceLocation("firework"), (p_239424_0_, p_239424_1_, p_239424_2_) -> p_239424_2_ != null && CrossbowItem.isCharged(p_239424_0_) && CrossbowItem.hasChargedProjectile(p_239424_0_, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
        ItemModelsProperties.registerProperty(Items.ELYTRA, new ResourceLocation("broken"), (p_239423_0_, p_239423_1_, p_239423_2_) -> ElytraItem.isUsable(p_239423_0_) ? 0.0f : 1.0f);
        ItemModelsProperties.registerProperty(Items.FISHING_ROD, new ResourceLocation("cast"), (p_239422_0_, p_239422_1_, p_239422_2_) -> {
            boolean flag1;
            if (p_239422_2_ == null) {
                return 0.0f;
            }
            boolean flag = p_239422_2_.getHeldItemMainhand() == p_239422_0_;
            boolean bl = flag1 = p_239422_2_.getHeldItemOffhand() == p_239422_0_;
            if (p_239422_2_.getHeldItemMainhand().getItem() instanceof FishingRodItem) {
                flag1 = false;
            }
            return (flag || flag1) && p_239422_2_ instanceof PlayerEntity && ((PlayerEntity)p_239422_2_).fishingBobber != null ? 1.0f : 0.0f;
        });
        ItemModelsProperties.registerProperty(Items.SHIELD, new ResourceLocation("blocking"), (p_239421_0_, p_239421_1_, p_239421_2_) -> p_239421_2_ != null && p_239421_2_.isHandActive() && p_239421_2_.getActiveItemStack() == p_239421_0_ ? 1.0f : 0.0f);
        ItemModelsProperties.registerProperty(Items.TRIDENT, new ResourceLocation("throwing"), (p_239419_0_, p_239419_1_, p_239419_2_) -> p_239419_2_ != null && p_239419_2_.isHandActive() && p_239419_2_.getActiveItemStack() == p_239419_0_ ? 1.0f : 0.0f);
    }

    static class Angle {
        private double field_239445_a_;
        private double field_239446_b_;
        private long field_239447_c_;

        private Angle() {
        }

        private boolean func_239448_a_(long p_239448_1_) {
            return this.field_239447_c_ != p_239448_1_;
        }

        private void func_239449_a_(long p_239449_1_, double p_239449_3_) {
            this.field_239447_c_ = p_239449_1_;
            double d0 = p_239449_3_ - this.field_239445_a_;
            d0 = MathHelper.positiveModulo(d0 + 0.5, 1.0) - 0.5;
            this.field_239446_b_ += d0 * 0.1;
            this.field_239446_b_ *= 0.8;
            this.field_239445_a_ = MathHelper.positiveModulo(this.field_239445_a_ + this.field_239446_b_, 1.0);
        }
    }
}
