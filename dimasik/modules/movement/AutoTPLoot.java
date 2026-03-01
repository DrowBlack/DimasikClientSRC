package dimasik.modules.movement;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class AutoTPLoot
extends Module {
    private final Minecraft mc = Minecraft.getInstance();
    private ScheduledExecutorService executorService;
    private static final double SEARCH_RADIUS = 100.0;
    private Vector3d initialPosition = null;
    private boolean isTeleported = false;
    private List<ItemStack> previousInventory = new ArrayList<ItemStack>();
    private ScheduledFuture<?> inventoryCheckTask;
    private ItemEntity currentTarget = null;
    private long lastActionTime = 0L;
    private final Set<Item> targetItems = new HashSet<Item>(Arrays.asList(Items.GOLDEN_APPLE, Items.NETHERITE_SWORD, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_HELMET, Items.NETHERITE_BOOTS, Items.NETHERITE_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET, Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE, Items.ELYTRA, Items.ENCHANTED_GOLDEN_APPLE, Items.PLAYER_HEAD, Items.TOTEM_OF_UNDYING, Items.SPLASH_POTION, Items.PHANTOM_MEMBRANE, Items.CONDUIT, Items.BONE_MEAL, Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX));

    public AutoTPLoot() {
        super("AutoLoot", Category.MOVEMENT);
    }

    @Override
    public void onEnabled() {
        this.saveCurrentInventory();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(this::findAndTeleport, 0L, 10L, TimeUnit.MILLISECONDS);
        this.inventoryCheckTask = this.executorService.scheduleAtFixedRate(this::checkInventory, 0L, 10L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDisabled() {
        this.stopTeleportation();
    }

    private void saveCurrentInventory() {
        this.previousInventory.clear();
        if (this.mc.player != null) {
            for (int i = 0; i < 36; ++i) {
                this.previousInventory.add(this.mc.player.inventory.getStackInSlot(i).copy());
            }
        }
    }

    private boolean hasNewTargetItems() {
        if (this.mc.player == null) {
            return false;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack currentStack = this.mc.player.inventory.getStackInSlot(i);
            if (currentStack.isEmpty() || !this.targetItems.contains(currentStack.getItem())) continue;
            boolean wasInInventory = false;
            for (ItemStack prevStack : this.previousInventory) {
                if (prevStack.isEmpty() || prevStack.getItem() != currentStack.getItem() || prevStack.getCount() < currentStack.getCount()) continue;
                wasInInventory = true;
                break;
            }
            if (wasInInventory) continue;
            return true;
        }
        return false;
    }

    private int getGoldenAppleCount() {
        if (this.mc.player == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = this.mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() != Items.GOLDEN_APPLE) continue;
            count += stack.getCount();
        }
        return count;
    }

    private boolean hasBlockAbove(ItemEntity item) {
        if (this.mc.world == null) {
            return false;
        }
        BlockPos itemPos = new BlockPos(item.getPositionVec());
        BlockPos abovePos = itemPos.up();
        BlockState aboveState = this.mc.world.getBlockState(abovePos);
        return !aboveState.isAir() && aboveState.getBlock() != Blocks.AIR;
    }

    private Vector3d findNearestFreePosition(Vector3d itemPos) {
        if (this.mc.world == null) {
            return itemPos;
        }
        BlockPos itemBlockPos = new BlockPos(itemPos);
        ArrayList<Vector3d> possiblePositions = new ArrayList<Vector3d>();
        for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; ++z) {
                BlockPos checkPos = new BlockPos(itemBlockPos.getX() + x, itemBlockPos.getY(), itemBlockPos.getZ() + z);
                BlockState state = this.mc.world.getBlockState(checkPos);
                BlockState aboveState = this.mc.world.getBlockState(checkPos.up());
                if (!state.isAir() || !aboveState.isAir()) continue;
                possiblePositions.add(new Vector3d((double)checkPos.getX() + 0.5, checkPos.getY(), (double)checkPos.getZ() + 0.5));
            }
        }
        if (!possiblePositions.isEmpty()) {
            return possiblePositions.stream().min(Comparator.comparingDouble(pos -> pos.squareDistanceTo(this.mc.player.getPositionVec()))).orElse(itemPos);
        }
        return itemPos;
    }

    private void findAndTeleport() {
        if (this.mc.world == null || this.mc.player == null || this.isTeleported) {
            return;
        }
        Optional<ItemEntity> targetOpt = this.findNearestTargetItem();
        if (targetOpt.isPresent()) {
            Vector3d teleportPos;
            ItemEntity item = targetOpt.get();
            if (item.getItem().getItem() == Items.GOLDEN_APPLE) {
                int currentGapples = this.getGoldenAppleCount();
                int itemCount = item.getItem().getCount();
                if (currentGapples < 10) {
                    return;
                }
            }
            this.currentTarget = item;
            if (this.initialPosition == null) {
                this.initialPosition = this.mc.player.getPositionVec();
                this.saveCurrentInventory();
            }
            this.mc.player.setMotion(0.0, 0.0, 0.0);
            this.mc.player.velocityChanged = true;
            Vector3d itemPos = item.getPositionVec();
            if (this.hasBlockAbove(item)) {
                teleportPos = this.findNearestFreePosition(itemPos);
            } else {
                double teleportX = Math.floor(itemPos.getX()) + 0.5;
                double teleportZ = Math.floor(itemPos.getZ()) + 0.5;
                double teleportY = Math.floor(itemPos.getY());
                teleportPos = new Vector3d(teleportX, teleportY, teleportZ);
            }
            this.teleport(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
            this.isTeleported = true;
            this.lastActionTime = System.currentTimeMillis();
        }
    }

    private void checkInventory() {
        if (!this.isTeleported || this.mc.player == null) {
            return;
        }
        if (System.currentTimeMillis() - this.lastActionTime > 150L) {
            this.teleportBack();
        }
    }

    private void teleport(double x, double y, double z) {
        y = Math.floor(y);
        this.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, true));
        this.mc.player.setPosition(x, y, z);
        this.lastActionTime = System.currentTimeMillis();
    }

    private void teleportBack() {
        if (this.initialPosition == null) {
            return;
        }
        this.mc.player.setMotion(0.0, 0.0, 0.0);
        this.mc.player.velocityChanged = true;
        double returnX = Math.floor(this.initialPosition.getX()) + 0.5;
        double returnZ = Math.floor(this.initialPosition.getZ()) + 0.5;
        double returnY = Math.floor(this.initialPosition.getY());
        this.teleport(returnX, returnY, returnZ);
        this.initialPosition = null;
        this.isTeleported = false;
        this.currentTarget = null;
        this.saveCurrentInventory();
    }

    private void stopTeleportation() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
            this.executorService = null;
        }
        if (this.inventoryCheckTask != null) {
            this.inventoryCheckTask.cancel(true);
        }
        this.initialPosition = null;
        this.isTeleported = false;
    }

    private Optional<ItemEntity> findNearestTargetItem() {
        if (this.mc.world == null || this.mc.player == null) {
            return Optional.empty();
        }
        return this.mc.world.getEntitiesWithinAABB(ItemEntity.class, this.mc.player.getBoundingBox().grow(100.0), e -> this.targetItems.contains(e.getItem().getItem()) && e.isAlive()).stream().min(Comparator.comparingDouble(e -> e.getDistanceSq(this.mc.player)));
    }
}
