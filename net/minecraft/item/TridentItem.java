package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class TridentItem
extends Item
implements IVanishable {
    private final Multimap<Attribute, AttributeModifier> tridentAttributes;

    public TridentItem(Item.Properties builderIn) {
        super(builderIn);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)-2.9f, AttributeModifier.Operation.ADDITION));
        this.tridentAttributes = builder.build();
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            int j;
            PlayerEntity playerentity = (PlayerEntity)entityLiving;
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10 && ((j = EnchantmentHelper.getRiptideModifier(stack)) <= 0 || playerentity.isWet())) {
                if (!worldIn.isRemote) {
                    stack.damageItem(1, playerentity, player -> player.sendBreakAnimation(entityLiving.getActiveHand()));
                    if (j == 0) {
                        TridentEntity tridententity = new TridentEntity(worldIn, (LivingEntity)playerentity, stack);
                        tridententity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0f, 2.5f + (float)j * 0.5f, 1.0f);
                        if (playerentity.abilities.isCreativeMode) {
                            tridententity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }
                        worldIn.addEntity(tridententity);
                        worldIn.playMovingSound(null, tridententity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        if (!playerentity.abilities.isCreativeMode) {
                            playerentity.inventory.deleteStack(stack);
                        }
                    }
                }
                playerentity.addStat(Stats.ITEM_USED.get(this));
                if (j > 0) {
                    float f7 = playerentity.rotationYaw;
                    float f = playerentity.rotationPitch;
                    float f1 = -MathHelper.sin(f7 * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
                    float f2 = -MathHelper.sin(f * ((float)Math.PI / 180));
                    float f3 = MathHelper.cos(f7 * ((float)Math.PI / 180)) * MathHelper.cos(f * ((float)Math.PI / 180));
                    float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                    float f5 = 3.0f * ((1.0f + (float)j) / 4.0f);
                    playerentity.addVelocity(f1 *= f5 / f4, f2 *= f5 / f4, f3 *= f5 / f4);
                    playerentity.startSpinAttack(20);
                    if (playerentity.isOnGround()) {
                        float f6 = 1.1999999f;
                        playerentity.move(MoverType.SELF, new Vector3d(0.0, 1.1999999284744263, 0.0));
                    }
                    SoundEvent soundevent = j >= 3 ? SoundEvents.ITEM_TRIDENT_RIPTIDE_3 : (j == 2 ? SoundEvents.ITEM_TRIDENT_RIPTIDE_2 : SoundEvents.ITEM_TRIDENT_RIPTIDE_1);
                    worldIn.playMovingSound(null, playerentity, soundevent, SoundCategory.PLAYERS, 1.0f, 1.0f);
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (itemstack.getDamage() >= itemstack.getMaxDamage() - 1) {
            return ActionResult.resultFail(itemstack);
        }
        if (EnchantmentHelper.getRiptideModifier(itemstack) > 0 && !playerIn.isWet()) {
            return ActionResult.resultFail(itemstack);
        }
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(1, attacker, entity -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if ((double)state.getBlockHardness(worldIn, pos) != 0.0) {
            stack.damageItem(2, entityLiving, entity -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.tridentAttributes : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}
