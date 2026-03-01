package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CrossbowItem
extends ShootableItem
implements IVanishable {
    private boolean isLoadingStart = false;
    private boolean isLoadingMiddle = false;

    public CrossbowItem(Item.Properties propertiesIn) {
        super(propertiesIn);
    }

    @Override
    public Predicate<ItemStack> getAmmoPredicate() {
        return ARROWS_OR_FIREWORKS;
    }

    @Override
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return ARROWS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (CrossbowItem.isCharged(itemstack)) {
            CrossbowItem.fireProjectiles(worldIn, playerIn, handIn, itemstack, CrossbowItem.func_220013_l(itemstack), 1.0f);
            CrossbowItem.setCharged(itemstack, false);
            return ActionResult.resultConsume(itemstack);
        }
        if (!playerIn.findAmmo(itemstack).isEmpty()) {
            if (!CrossbowItem.isCharged(itemstack)) {
                this.isLoadingStart = false;
                this.isLoadingMiddle = false;
                playerIn.setActiveHand(handIn);
            }
            return ActionResult.resultConsume(itemstack);
        }
        return ActionResult.resultFail(itemstack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        int i = this.getUseDuration(stack) - timeLeft;
        float f = CrossbowItem.getCharge(i, stack);
        if (f >= 1.0f && !CrossbowItem.isCharged(stack) && CrossbowItem.hasAmmo(entityLiving, stack)) {
            CrossbowItem.setCharged(stack, true);
            SoundCategory soundcategory = entityLiving instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            worldIn.playSound(null, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundcategory, 1.0f, 1.0f / (random.nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    private static boolean hasAmmo(LivingEntity entityIn, ItemStack stack) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, stack);
        int j = i == 0 ? 1 : 3;
        boolean flag = entityIn instanceof PlayerEntity && ((PlayerEntity)entityIn).abilities.isCreativeMode;
        ItemStack itemstack = entityIn.findAmmo(stack);
        ItemStack itemstack1 = itemstack.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemstack = itemstack1.copy();
            }
            if (itemstack.isEmpty() && flag) {
                itemstack = new ItemStack(Items.ARROW);
                itemstack1 = itemstack.copy();
            }
            if (CrossbowItem.func_220023_a(entityIn, stack, itemstack, k > 0, flag)) continue;
            return false;
        }
        return true;
    }

    private static boolean func_220023_a(LivingEntity p_220023_0_, ItemStack stack, ItemStack p_220023_2_, boolean p_220023_3_, boolean p_220023_4_) {
        ItemStack itemstack;
        boolean flag;
        if (p_220023_2_.isEmpty()) {
            return false;
        }
        boolean bl = flag = p_220023_4_ && p_220023_2_.getItem() instanceof ArrowItem;
        if (!(flag || p_220023_4_ || p_220023_3_)) {
            itemstack = p_220023_2_.split(1);
            if (p_220023_2_.isEmpty() && p_220023_0_ instanceof PlayerEntity) {
                ((PlayerEntity)p_220023_0_).inventory.deleteStack(p_220023_2_);
            }
        } else {
            itemstack = p_220023_2_.copy();
        }
        CrossbowItem.addChargedProjectile(stack, itemstack);
        return true;
    }

    public static boolean isCharged(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTag();
        return compoundnbt != null && compoundnbt.getBoolean("Charged");
    }

    public static void setCharged(ItemStack stack, boolean chargedIn) {
        CompoundNBT compoundnbt = stack.getOrCreateTag();
        compoundnbt.putBoolean("Charged", chargedIn);
    }

    private static void addChargedProjectile(ItemStack crossbow, ItemStack projectile) {
        CompoundNBT compoundnbt = crossbow.getOrCreateTag();
        ListNBT listnbt = compoundnbt.contains("ChargedProjectiles", 9) ? compoundnbt.getList("ChargedProjectiles", 10) : new ListNBT();
        CompoundNBT compoundnbt1 = new CompoundNBT();
        projectile.write(compoundnbt1);
        listnbt.add(compoundnbt1);
        compoundnbt.put("ChargedProjectiles", listnbt);
    }

    private static List<ItemStack> getChargedProjectiles(ItemStack stack) {
        ListNBT listnbt;
        ArrayList<ItemStack> list = Lists.newArrayList();
        CompoundNBT compoundnbt = stack.getTag();
        if (compoundnbt != null && compoundnbt.contains("ChargedProjectiles", 9) && (listnbt = compoundnbt.getList("ChargedProjectiles", 10)) != null) {
            for (int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                list.add(ItemStack.read(compoundnbt1));
            }
        }
        return list;
    }

    private static void clearProjectiles(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getTag();
        if (compoundnbt != null) {
            ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 9);
            listnbt.clear();
            compoundnbt.put("ChargedProjectiles", listnbt);
        }
    }

    public static boolean hasChargedProjectile(ItemStack stack, Item ammoItem) {
        return CrossbowItem.getChargedProjectiles(stack).stream().anyMatch(p_220010_1_ -> p_220010_1_.getItem() == ammoItem);
    }

    private static void fireProjectile(World worldIn, LivingEntity shooter, Hand handIn, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean isCreativeMode, float velocity, float inaccuracy, float projectileAngle) {
        if (!worldIn.isRemote) {
            ProjectileEntity projectileentity;
            boolean flag;
            boolean bl = flag = projectile.getItem() == Items.FIREWORK_ROCKET;
            if (flag) {
                projectileentity = new FireworkRocketEntity(worldIn, projectile, shooter, shooter.getPosX(), shooter.getPosYEye() - (double)0.15f, shooter.getPosZ(), true);
            } else {
                projectileentity = CrossbowItem.createArrow(worldIn, shooter, crossbow, projectile);
                if (isCreativeMode || projectileAngle != 0.0f) {
                    ((AbstractArrowEntity)projectileentity).pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                }
            }
            if (shooter instanceof ICrossbowUser) {
                ICrossbowUser icrossbowuser = (ICrossbowUser)((Object)shooter);
                icrossbowuser.func_230284_a_(icrossbowuser.getAttackTarget(), crossbow, projectileentity, projectileAngle);
            } else {
                Vector3d vector3d1 = shooter.getUpVector(1.0f);
                Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), projectileAngle, true);
                Vector3d vector3d = shooter.getLook(1.0f);
                Vector3f vector3f = new Vector3f(vector3d);
                vector3f.transform(quaternion);
                projectileentity.shoot(vector3f.getX(), vector3f.getY(), vector3f.getZ(), velocity, inaccuracy);
            }
            crossbow.damageItem(flag ? 3 : 1, shooter, p_220017_1_ -> p_220017_1_.sendBreakAnimation(handIn));
            worldIn.addEntity(projectileentity);
            worldIn.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
        }
    }

    private static AbstractArrowEntity createArrow(World worldIn, LivingEntity shooter, ItemStack crossbow, ItemStack ammo) {
        ArrowItem arrowitem = (ArrowItem)(ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, ammo, shooter);
        if (shooter instanceof PlayerEntity) {
            abstractarrowentity.setIsCritical(true);
        }
        abstractarrowentity.setHitSound(SoundEvents.ITEM_CROSSBOW_HIT);
        abstractarrowentity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            abstractarrowentity.setPierceLevel((byte)i);
        }
        return abstractarrowentity;
    }

    public static void fireProjectiles(World worldIn, LivingEntity shooter, Hand handIn, ItemStack stack, float velocityIn, float inaccuracyIn) {
        List<ItemStack> list = CrossbowItem.getChargedProjectiles(stack);
        float[] afloat = CrossbowItem.getRandomSoundPitches(shooter.getRNG());
        for (int i = 0; i < list.size(); ++i) {
            boolean flag;
            ItemStack itemstack = list.get(i);
            boolean bl = flag = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.isCreativeMode;
            if (itemstack.isEmpty()) continue;
            if (i == 0) {
                CrossbowItem.fireProjectile(worldIn, shooter, handIn, stack, itemstack, afloat[i], flag, velocityIn, inaccuracyIn, 0.0f);
                continue;
            }
            if (i == 1) {
                CrossbowItem.fireProjectile(worldIn, shooter, handIn, stack, itemstack, afloat[i], flag, velocityIn, inaccuracyIn, -10.0f);
                continue;
            }
            if (i != 2) continue;
            CrossbowItem.fireProjectile(worldIn, shooter, handIn, stack, itemstack, afloat[i], flag, velocityIn, inaccuracyIn, 10.0f);
        }
        CrossbowItem.fireProjectilesAfter(worldIn, shooter, stack);
    }

    private static float[] getRandomSoundPitches(Random rand) {
        boolean flag = rand.nextBoolean();
        return new float[]{1.0f, CrossbowItem.getRandomSoundPitch(flag), CrossbowItem.getRandomSoundPitch(!flag)};
    }

    private static float getRandomSoundPitch(boolean flagIn) {
        float f = flagIn ? 0.63f : 0.43f;
        return 1.0f / (random.nextFloat() * 0.5f + 1.8f) + f;
    }

    private static void fireProjectilesAfter(World worldIn, LivingEntity shooter, ItemStack stack) {
        if (shooter instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)shooter;
            if (!worldIn.isRemote) {
                CriteriaTriggers.SHOT_CROSSBOW.test(serverplayerentity, stack);
            }
            serverplayerentity.addStat(Stats.ITEM_USED.get(stack.getItem()));
        }
        CrossbowItem.clearProjectiles(stack);
    }

    @Override
    public void onUse(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
        if (!worldIn.isRemote) {
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
            SoundEvent soundevent = this.getSoundEvent(i);
            SoundEvent soundevent1 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(stack.getUseDuration() - count) / (float)CrossbowItem.getChargeTime(stack);
            if (f < 0.2f) {
                this.isLoadingStart = false;
                this.isLoadingMiddle = false;
            }
            if (f >= 0.2f && !this.isLoadingStart) {
                this.isLoadingStart = true;
                worldIn.playSound(null, livingEntityIn.getPosX(), livingEntityIn.getPosY(), livingEntityIn.getPosZ(), soundevent, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            if (f >= 0.5f && soundevent1 != null && !this.isLoadingMiddle) {
                this.isLoadingMiddle = true;
                worldIn.playSound(null, livingEntityIn.getPosX(), livingEntityIn.getPosY(), livingEntityIn.getPosZ(), soundevent1, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return CrossbowItem.getChargeTime(stack) + 3;
    }

    public static int getChargeTime(ItemStack stack) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        return i == 0 ? 25 : 25 - 5 * i;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    private SoundEvent getSoundEvent(int enchantmentLevel) {
        switch (enchantmentLevel) {
            case 1: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
            }
            case 2: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
            }
            case 3: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
            }
        }
        return SoundEvents.ITEM_CROSSBOW_LOADING_START;
    }

    private static float getCharge(int useTime, ItemStack stack) {
        float f = (float)useTime / (float)CrossbowItem.getChargeTime(stack);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        List<ItemStack> list = CrossbowItem.getChargedProjectiles(stack);
        if (CrossbowItem.isCharged(stack) && !list.isEmpty()) {
            ItemStack itemstack = list.get(0);
            tooltip.add(new TranslationTextComponent("item.minecraft.crossbow.projectile").appendString(" ").append(itemstack.getTextComponent()));
            if (flagIn.isAdvanced() && itemstack.getItem() == Items.FIREWORK_ROCKET) {
                ArrayList<ITextComponent> list1 = Lists.newArrayList();
                Items.FIREWORK_ROCKET.addInformation(itemstack, worldIn, list1, flagIn);
                if (!list1.isEmpty()) {
                    for (int i = 0; i < list1.size(); ++i) {
                        list1.set(i, new StringTextComponent("  ").append((ITextComponent)list1.get(i)).mergeStyle(TextFormatting.GRAY));
                    }
                    tooltip.addAll(list1);
                }
            }
        }
    }

    private static float func_220013_l(ItemStack p_220013_0_) {
        return p_220013_0_.getItem() == Items.CROSSBOW && CrossbowItem.hasChargedProjectile(p_220013_0_, Items.FIREWORK_ROCKET) ? 1.6f : 3.15f;
    }

    @Override
    public int func_230305_d_() {
        return 8;
    }
}
