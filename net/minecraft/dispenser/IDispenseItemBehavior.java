package net.minecraft.dispenser;

import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.WitherSkeletonSkullBlock;
import net.minecraft.dispenser.BeehiveDispenseBehavior;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.DispenseBoatBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IDispenseItemBehavior {
    public static final IDispenseItemBehavior NOOP = (source, stack) -> stack;

    public ItemStack dispense(IBlockSource var1, ItemStack var2);

    public static void init() {
        DispenserBlock.registerDispenseBehavior(Items.ARROW, new ProjectileDispenseBehavior(){

            @Override
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                ArrowEntity arrowentity = new ArrowEntity(worldIn, position.getX(), position.getY(), position.getZ());
                arrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
                return arrowentity;
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.TIPPED_ARROW, new ProjectileDispenseBehavior(){

            @Override
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                ArrowEntity arrowentity = new ArrowEntity(worldIn, position.getX(), position.getY(), position.getZ());
                arrowentity.setPotionEffect(stackIn);
                arrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
                return arrowentity;
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.SPECTRAL_ARROW, new ProjectileDispenseBehavior(){

            @Override
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                SpectralArrowEntity abstractarrowentity = new SpectralArrowEntity(worldIn, position.getX(), position.getY(), position.getZ());
                abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
                return abstractarrowentity;
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.EGG, new ProjectileDispenseBehavior(){

            @Override
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return Util.make(new EggEntity(worldIn, position.getX(), position.getY(), position.getZ()), egg -> egg.setItem(stackIn));
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.SNOWBALL, new ProjectileDispenseBehavior(){

            @Override
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return Util.make(new SnowballEntity(worldIn, position.getX(), position.getY(), position.getZ()), snowball -> snowball.setItem(stackIn));
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.EXPERIENCE_BOTTLE, new ProjectileDispenseBehavior(){

            @Override
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return Util.make(new ExperienceBottleEntity(worldIn, position.getX(), position.getY(), position.getZ()), experienceBottle -> experienceBottle.setItem(stackIn));
            }

            @Override
            protected float getProjectileInaccuracy() {
                return super.getProjectileInaccuracy() * 0.5f;
            }

            @Override
            protected float getProjectileVelocity() {
                return super.getProjectileVelocity() * 1.25f;
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.SPLASH_POTION, new IDispenseItemBehavior(){

            @Override
            public ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
                return new ProjectileDispenseBehavior(){

                    @Override
                    protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                        return Util.make(new PotionEntity(worldIn, position.getX(), position.getY(), position.getZ()), potion -> potion.setItem(stackIn));
                    }

                    @Override
                    protected float getProjectileInaccuracy() {
                        return super.getProjectileInaccuracy() * 0.5f;
                    }

                    @Override
                    protected float getProjectileVelocity() {
                        return super.getProjectileVelocity() * 1.25f;
                    }
                }.dispense(p_dispense_1_, p_dispense_2_);
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.LINGERING_POTION, new IDispenseItemBehavior(){

            @Override
            public ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
                return new ProjectileDispenseBehavior(){

                    @Override
                    protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                        return Util.make(new PotionEntity(worldIn, position.getX(), position.getY(), position.getZ()), potion -> potion.setItem(stackIn));
                    }

                    @Override
                    protected float getProjectileInaccuracy() {
                        return super.getProjectileInaccuracy() * 0.5f;
                    }

                    @Override
                    protected float getProjectileVelocity() {
                        return super.getProjectileVelocity() * 1.25f;
                    }
                }.dispense(p_dispense_1_, p_dispense_2_);
            }
        });
        DefaultDispenseItemBehavior defaultdispenseitembehavior = new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                EntityType<?> entitytype = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());
                entitytype.spawn(source.getWorld(), stack, null, source.getBlockPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
                stack.shrink(1);
                return stack;
            }
        };
        for (SpawnEggItem spawneggitem : SpawnEggItem.getEggs()) {
            DispenserBlock.registerDispenseBehavior(spawneggitem, defaultdispenseitembehavior);
        }
        DispenserBlock.registerDispenseBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                BlockPos blockpos = source.getBlockPos().offset(direction);
                ServerWorld world = source.getWorld();
                ArmorStandEntity armorstandentity = new ArmorStandEntity(world, (double)blockpos.getX() + 0.5, blockpos.getY(), (double)blockpos.getZ() + 0.5);
                EntityType.applyItemNBT(world, null, armorstandentity, stack.getTag());
                armorstandentity.rotationYaw = direction.getHorizontalAngle();
                world.addEntity(armorstandentity);
                stack.shrink(1);
                return stack;
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.SADDLE, new OptionalDispenseBehavior(){

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                List<LivingEntity> list = source.getWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(blockpos), entity -> {
                    if (!(entity instanceof IEquipable)) {
                        return false;
                    }
                    IEquipable iequipable = (IEquipable)((Object)entity);
                    return !iequipable.isHorseSaddled() && iequipable.func_230264_L__();
                });
                if (!list.isEmpty()) {
                    ((IEquipable)((Object)list.get(0))).func_230266_a_(SoundCategory.BLOCKS);
                    stack.shrink(1);
                    this.setSuccessful(true);
                    return stack;
                }
                return super.dispenseStack(source, stack);
            }
        });
        OptionalDispenseBehavior defaultdispenseitembehavior1 = new OptionalDispenseBehavior(){

            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                for (AbstractHorseEntity abstracthorseentity : source.getWorld().getEntitiesWithinAABB(AbstractHorseEntity.class, new AxisAlignedBB(blockpos), horse -> horse.isAlive() && horse.func_230276_fq_())) {
                    if (!abstracthorseentity.isArmor(stack) || abstracthorseentity.func_230277_fr_() || !abstracthorseentity.isTame()) continue;
                    abstracthorseentity.replaceItemInInventory(401, stack.split(1));
                    this.setSuccessful(true);
                    return stack;
                }
                return super.dispenseStack(source, stack);
            }
        };
        DispenserBlock.registerDispenseBehavior(Items.LEATHER_HORSE_ARMOR, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.IRON_HORSE_ARMOR, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.GOLDEN_HORSE_ARMOR, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.DIAMOND_HORSE_ARMOR, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.WHITE_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.ORANGE_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.CYAN_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.BLUE_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.BROWN_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.BLACK_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.GRAY_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.GREEN_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.LIGHT_BLUE_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.LIGHT_GRAY_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.LIME_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.MAGENTA_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.PINK_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.PURPLE_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.RED_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.YELLOW_CARPET, defaultdispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.CHEST, new OptionalDispenseBehavior(){

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                for (AbstractChestedHorseEntity abstractchestedhorseentity : source.getWorld().getEntitiesWithinAABB(AbstractChestedHorseEntity.class, new AxisAlignedBB(blockpos), chestedHorse -> chestedHorse.isAlive() && !chestedHorse.hasChest())) {
                    if (!abstractchestedhorseentity.isTame() || !abstractchestedhorseentity.replaceItemInInventory(499, stack)) continue;
                    stack.shrink(1);
                    this.setSuccessful(true);
                    return stack;
                }
                return super.dispenseStack(source, stack);
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity((World)source.getWorld(), stack, source.getX(), source.getY(), source.getX(), true);
                IDispenseItemBehavior.dispenseEntity(source, fireworkrocketentity, direction);
                fireworkrocketentity.shoot(direction.getXOffset(), direction.getYOffset(), direction.getZOffset(), 0.5f, 1.0f);
                source.getWorld().addEntity(fireworkrocketentity);
                stack.shrink(1);
                return stack;
            }

            @Override
            protected void playDispenseSound(IBlockSource source) {
                source.getWorld().playEvent(1004, source.getBlockPos(), 0);
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior(){

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                IPosition iposition = DispenserBlock.getDispensePosition(source);
                double d0 = iposition.getX() + (double)((float)direction.getXOffset() * 0.3f);
                double d1 = iposition.getY() + (double)((float)direction.getYOffset() * 0.3f);
                double d2 = iposition.getZ() + (double)((float)direction.getZOffset() * 0.3f);
                ServerWorld world = source.getWorld();
                Random random = world.rand;
                double d3 = random.nextGaussian() * 0.05 + (double)direction.getXOffset();
                double d4 = random.nextGaussian() * 0.05 + (double)direction.getYOffset();
                double d5 = random.nextGaussian() * 0.05 + (double)direction.getZOffset();
                world.addEntity(Util.make(new SmallFireballEntity(world, d0, d1, d2, d3, d4, d5), fireball -> fireball.setStack(stack)));
                stack.shrink(1);
                return stack;
            }

            @Override
            protected void playDispenseSound(IBlockSource source) {
                source.getWorld().playEvent(1018, source.getBlockPos(), 0);
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.OAK_BOAT, new DispenseBoatBehavior(BoatEntity.Type.OAK));
        DispenserBlock.registerDispenseBehavior(Items.SPRUCE_BOAT, new DispenseBoatBehavior(BoatEntity.Type.SPRUCE));
        DispenserBlock.registerDispenseBehavior(Items.BIRCH_BOAT, new DispenseBoatBehavior(BoatEntity.Type.BIRCH));
        DispenserBlock.registerDispenseBehavior(Items.JUNGLE_BOAT, new DispenseBoatBehavior(BoatEntity.Type.JUNGLE));
        DispenserBlock.registerDispenseBehavior(Items.DARK_OAK_BOAT, new DispenseBoatBehavior(BoatEntity.Type.DARK_OAK));
        DispenserBlock.registerDispenseBehavior(Items.ACACIA_BOAT, new DispenseBoatBehavior(BoatEntity.Type.ACACIA));
        DefaultDispenseItemBehavior idispenseitembehavior1 = new DefaultDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                BucketItem bucketitem = (BucketItem)stack.getItem();
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                ServerWorld world = source.getWorld();
                if (bucketitem.tryPlaceContainedLiquid(null, world, blockpos, null)) {
                    bucketitem.onLiquidPlaced(world, stack, blockpos);
                    return new ItemStack(Items.BUCKET);
                }
                return this.defaultBehaviour.dispense(source, stack);
            }
        };
        DispenserBlock.registerDispenseBehavior(Items.LAVA_BUCKET, idispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.WATER_BUCKET, idispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.SALMON_BUCKET, idispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.COD_BUCKET, idispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.PUFFERFISH_BUCKET, idispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.TROPICAL_FISH_BUCKET, idispenseitembehavior1);
        DispenserBlock.registerDispenseBehavior(Items.BUCKET, new DefaultDispenseItemBehavior(){
            private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                BlockPos blockpos;
                ServerWorld iworld = source.getWorld();
                BlockState blockstate = iworld.getBlockState(blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING)));
                Block block = blockstate.getBlock();
                if (block instanceof IBucketPickupHandler) {
                    Fluid fluid = ((IBucketPickupHandler)((Object)block)).pickupFluid(iworld, blockpos, blockstate);
                    if (!(fluid instanceof FlowingFluid)) {
                        return super.dispenseStack(source, stack);
                    }
                    Item item = fluid.getFilledBucket();
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        return new ItemStack(item);
                    }
                    if (((DispenserTileEntity)source.getBlockTileEntity()).addItemStack(new ItemStack(item)) < 0) {
                        this.defaultBehaviour.dispense(source, new ItemStack(item));
                    }
                    return stack;
                }
                return super.dispenseStack(source, stack);
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseBehavior(){

            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                ServerWorld world = source.getWorld();
                this.setSuccessful(true);
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                BlockPos blockpos = source.getBlockPos().offset(direction);
                BlockState blockstate = world.getBlockState(blockpos);
                if (AbstractFireBlock.canLightBlock(world, blockpos, direction)) {
                    world.setBlockState(blockpos, AbstractFireBlock.getFireForPlacement(world, blockpos));
                } else if (CampfireBlock.canBeLit(blockstate)) {
                    world.setBlockState(blockpos, (BlockState)blockstate.with(BlockStateProperties.LIT, true));
                } else if (blockstate.getBlock() instanceof TNTBlock) {
                    TNTBlock.explode(world, blockpos);
                    world.removeBlock(blockpos, false);
                } else {
                    this.setSuccessful(false);
                }
                if (this.isSuccessful() && stack.attemptDamageItem(1, world.rand, null)) {
                    stack.setCount(0);
                }
                return stack;
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.BONE_MEAL, new OptionalDispenseBehavior(){

            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                this.setSuccessful(true);
                ServerWorld world = source.getWorld();
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                if (!BoneMealItem.applyBonemeal(stack, world, blockpos) && !BoneMealItem.growSeagrass(stack, world, blockpos, null)) {
                    this.setSuccessful(false);
                } else if (!world.isRemote) {
                    world.playEvent(2005, blockpos, 0);
                }
                return stack;
            }
        });
        DispenserBlock.registerDispenseBehavior(Blocks.TNT, new DefaultDispenseItemBehavior(){

            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                ServerWorld world = source.getWorld();
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                TNTEntity tntentity = new TNTEntity(world, (double)blockpos.getX() + 0.5, blockpos.getY(), (double)blockpos.getZ() + 0.5, null);
                world.addEntity(tntentity);
                ((World)world).playSound(null, tntentity.getPosX(), tntentity.getPosY(), tntentity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.shrink(1);
                return stack;
            }
        });
        OptionalDispenseBehavior idispenseitembehavior = new OptionalDispenseBehavior(){

            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                this.setSuccessful(ArmorItem.func_226626_a_(source, stack));
                return stack;
            }
        };
        DispenserBlock.registerDispenseBehavior(Items.CREEPER_HEAD, idispenseitembehavior);
        DispenserBlock.registerDispenseBehavior(Items.ZOMBIE_HEAD, idispenseitembehavior);
        DispenserBlock.registerDispenseBehavior(Items.DRAGON_HEAD, idispenseitembehavior);
        DispenserBlock.registerDispenseBehavior(Items.SKELETON_SKULL, idispenseitembehavior);
        DispenserBlock.registerDispenseBehavior(Items.PLAYER_HEAD, idispenseitembehavior);
        DispenserBlock.registerDispenseBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseBehavior(){

            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                ServerWorld world = source.getWorld();
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                BlockPos blockpos = source.getBlockPos().offset(direction);
                if (world.isAirBlock(blockpos) && WitherSkeletonSkullBlock.canSpawnMob(world, blockpos, stack)) {
                    world.setBlockState(blockpos, (BlockState)Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().getHorizontalIndex() * 4), 3);
                    TileEntity tileentity = world.getTileEntity(blockpos);
                    if (tileentity instanceof SkullTileEntity) {
                        WitherSkeletonSkullBlock.checkWitherSpawn(world, blockpos, (SkullTileEntity)tileentity);
                    }
                    stack.shrink(1);
                    this.setSuccessful(true);
                } else {
                    this.setSuccessful(ArmorItem.func_226626_a_(source, stack));
                }
                return stack;
            }
        });
        DispenserBlock.registerDispenseBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseBehavior(){

            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                ServerWorld world = source.getWorld();
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                CarvedPumpkinBlock carvedpumpkinblock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
                if (world.isAirBlock(blockpos) && carvedpumpkinblock.canDispenserPlace(world, blockpos)) {
                    if (!world.isRemote) {
                        world.setBlockState(blockpos, carvedpumpkinblock.getDefaultState(), 3);
                    }
                    stack.shrink(1);
                    this.setSuccessful(true);
                } else {
                    this.setSuccessful(ArmorItem.func_226626_a_(source, stack));
                }
                return stack;
            }
        });
        DispenserBlock.registerDispenseBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());
        for (DyeColor dyecolor : DyeColor.values()) {
            DispenserBlock.registerDispenseBehavior(ShulkerBoxBlock.getBlockByColor(dyecolor).asItem(), new ShulkerBoxDispenseBehavior());
        }
        DispenserBlock.registerDispenseBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseBehavior(){
            private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

            private ItemStack glassBottleFill(IBlockSource source, ItemStack empty, ItemStack filled) {
                empty.shrink(1);
                if (empty.isEmpty()) {
                    return filled.copy();
                }
                if (((DispenserTileEntity)source.getBlockTileEntity()).addItemStack(filled.copy()) < 0) {
                    this.defaultBehaviour.dispense(source, filled.copy());
                }
                return empty;
            }

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                this.setSuccessful(false);
                ServerWorld serverworld = source.getWorld();
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                BlockState blockstate = serverworld.getBlockState(blockpos);
                if (blockstate.isInAndMatches(BlockTags.BEEHIVES, state -> state.hasProperty(BeehiveBlock.HONEY_LEVEL)) && blockstate.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    ((BeehiveBlock)blockstate.getBlock()).takeHoney(serverworld, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                    this.setSuccessful(true);
                    return this.glassBottleFill(source, stack, new ItemStack(Items.HONEY_BOTTLE));
                }
                if (serverworld.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
                    this.setSuccessful(true);
                    return this.glassBottleFill(source, stack, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER));
                }
                return super.dispenseStack(source, stack);
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.GLOWSTONE, new OptionalDispenseBehavior(){

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().get(DispenserBlock.FACING);
                BlockPos blockpos = source.getBlockPos().offset(direction);
                ServerWorld world = source.getWorld();
                BlockState blockstate = world.getBlockState(blockpos);
                this.setSuccessful(true);
                if (blockstate.isIn(Blocks.RESPAWN_ANCHOR)) {
                    if (blockstate.get(RespawnAnchorBlock.CHARGES) != 4) {
                        RespawnAnchorBlock.chargeAnchor(world, blockpos, blockstate);
                        stack.shrink(1);
                    } else {
                        this.setSuccessful(false);
                    }
                    return stack;
                }
                return super.dispenseStack(source, stack);
            }
        });
        DispenserBlock.registerDispenseBehavior(Items.SHEARS.asItem(), new BeehiveDispenseBehavior());
    }

    public static void dispenseEntity(IBlockSource source, Entity entity, Direction direction) {
        entity.setPosition(source.getX() + (double)direction.getXOffset() * (0.5000099999997474 - (double)entity.getWidth() / 2.0), source.getY() + (double)direction.getYOffset() * (0.5000099999997474 - (double)entity.getHeight() / 2.0) - (double)entity.getHeight() / 2.0, source.getZ() + (double)direction.getZOffset() * (0.5000099999997474 - (double)entity.getWidth() / 2.0));
    }
}
