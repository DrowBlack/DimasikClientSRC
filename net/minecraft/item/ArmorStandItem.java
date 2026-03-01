package net.minecraft.item;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ArmorStandItem
extends Item {
    public ArmorStandItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        Direction direction = context.getFace();
        if (direction == Direction.DOWN) {
            return ActionResultType.FAIL;
        }
        World world = context.getWorld();
        BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
        BlockPos blockpos = blockitemusecontext.getPos();
        ItemStack itemstack = context.getItem();
        Vector3d vector3d = Vector3d.copyCenteredHorizontally(blockpos);
        AxisAlignedBB axisalignedbb = EntityType.ARMOR_STAND.getSize().func_242285_a(vector3d.getX(), vector3d.getY(), vector3d.getZ());
        if (world.hasNoCollisions(null, axisalignedbb, p_242390_0_ -> true) && world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb).isEmpty()) {
            if (world instanceof ServerWorld) {
                ServerWorld serverworld = (ServerWorld)world;
                ArmorStandEntity armorstandentity = EntityType.ARMOR_STAND.create(serverworld, itemstack.getTag(), null, context.getPlayer(), blockpos, SpawnReason.SPAWN_EGG, true, true);
                if (armorstandentity == null) {
                    return ActionResultType.FAIL;
                }
                serverworld.func_242417_l(armorstandentity);
                float f = (float)MathHelper.floor((MathHelper.wrapDegrees(context.getPlacementYaw() - 180.0f) + 22.5f) / 45.0f) * 45.0f;
                armorstandentity.setLocationAndAngles(armorstandentity.getPosX(), armorstandentity.getPosY(), armorstandentity.getPosZ(), f, 0.0f);
                this.applyRandomRotations(armorstandentity, world.rand);
                world.addEntity(armorstandentity);
                world.playSound(null, armorstandentity.getPosX(), armorstandentity.getPosY(), armorstandentity.getPosZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75f, 0.8f);
            }
            itemstack.shrink(1);
            return ActionResultType.func_233537_a_(world.isRemote);
        }
        return ActionResultType.FAIL;
    }

    private void applyRandomRotations(ArmorStandEntity armorStand, Random rand) {
        Rotations rotations = armorStand.getHeadRotation();
        float f = rand.nextFloat() * 5.0f;
        float f1 = rand.nextFloat() * 20.0f - 10.0f;
        Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
        armorStand.setHeadRotation(rotations1);
        rotations = armorStand.getBodyRotation();
        f = rand.nextFloat() * 10.0f - 5.0f;
        rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
        armorStand.setBodyRotation(rotations1);
    }
}
