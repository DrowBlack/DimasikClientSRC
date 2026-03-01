package dimasik.modules.combat;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventMoveInput;
import dimasik.events.main.movement.EventJump;
import dimasik.events.main.movement.EventStrafe;
import dimasik.events.main.player.EventObsidianPlace;
import dimasik.events.main.player.EventSwimming;
import dimasik.events.main.player.EventSync;
import dimasik.helpers.module.aura.AuraHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.modules.movement.AutoSprint;
import dimasik.utils.math.MathUtils;
import dimasik.utils.time.TimerUtils;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class CrystalAura
extends Module {
    private final CheckboxOption safeYourSelf = new CheckboxOption("\u041d\u0435 \u0432\u0437\u0440\u044b\u0432\u0430\u0442\u044c \u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", false);
    private Entity crystalEntity = null;
    private BlockPos obsidianPos = null;
    private int oldCurrentSlot = -1;
    private Vector2f rotationVector = null;
    TimerUtils attackStopWatch = new TimerUtils();
    int bestSlot = -1;
    int oldSlot = -1;
    private final AuraHelpers auraHelpers = new AuraHelpers();
    private final EventListener<EventObsidianPlace> obsidian = this::onObsidianPlace;
    private final EventListener<EventUpdate> update = this::onUpdate;
    private final EventListener<EventSync> sync = this::onMotion;
    private final EventListener<EventMoveInput> input = this::input;
    private final EventListener<EventStrafe> strafe = this::strafe;
    private final EventListener<EventJump> jump = this::jump;
    private final EventListener<EventSwimming> swim = this::swim;

    public CrystalAura() {
        super("AutoExplosion", Category.COMBAT);
        this.settings(new Option[0]);
    }

    public void input(EventMoveInput eventMoveInput) {
        if (this.rotationVector != null) {
            this.auraHelpers.fixMovement(eventMoveInput, this.rotationVector.x);
        }
    }

    public void strafe(EventStrafe event) {
        if (this.rotationVector != null) {
            event.setYaw(this.rotationVector.x);
        }
    }

    public void jump(EventJump event) {
        if (this.rotationVector != null) {
            event.setYaw(this.rotationVector.x);
        }
    }

    public void swim(EventSwimming event) {
        if (this.rotationVector != null) {
            event.setYaw(this.rotationVector.x);
            event.setPitch(this.rotationVector.y);
        }
    }

    public void onObsidianPlace(EventObsidianPlace e) {
        boolean slotNotNull;
        BlockPos obsidianPos = e.getPos();
        boolean isOffHand = CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        int slotInInventory = CrystalAura.getSlotInInventoryOrHotbar(Items.END_CRYSTAL, false);
        int slotInHotBar = CrystalAura.getSlotInInventoryOrHotbar(Items.END_CRYSTAL, true);
        this.bestSlot = this.findBestSlotInHotBar();
        boolean bl = slotNotNull = this.bestSlot != -1 && CrystalAura.mc.player.inventory.getStackInSlot(this.bestSlot).getItem() != Items.AIR;
        if (isOffHand && obsidianPos != null) {
            this.setAndUseCrystal(this.bestSlot, obsidianPos);
            this.obsidianPos = obsidianPos;
        }
        if (slotInHotBar == -1 && slotInInventory != -1 && this.bestSlot != -1) {
            CrystalAura.mc.playerController.windowClick(0, slotInInventory, this.bestSlot, ClickType.SWAP, CrystalAura.mc.player);
            if (slotNotNull && this.oldSlot == -1) {
                this.oldSlot = slotInInventory;
            }
            if (obsidianPos != null) {
                this.oldCurrentSlot = CrystalAura.mc.player.inventory.currentItem;
                this.setAndUseCrystal(this.bestSlot, obsidianPos);
                CrystalAura.mc.player.inventory.currentItem = this.oldCurrentSlot;
                this.obsidianPos = obsidianPos;
            }
            CrystalAura.mc.playerController.windowClick(0, this.oldSlot, this.bestSlot, ClickType.SWAP, CrystalAura.mc.player);
        } else if (slotInHotBar != -1 && obsidianPos != null) {
            this.oldCurrentSlot = CrystalAura.mc.player.inventory.currentItem;
            this.setAndUseCrystal(slotInHotBar, obsidianPos);
            CrystalAura.mc.player.inventory.currentItem = this.oldCurrentSlot;
            this.obsidianPos = obsidianPos;
        }
    }

    public int findBestSlotInHotBar() {
        int emptySlot = this.findEmptySlot();
        return emptySlot != -1 ? emptySlot : this.findNonSwordSlot();
    }

    private int findNonSwordSlot() {
        for (int i = 0; i < 9; ++i) {
            if (CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() instanceof SwordItem || CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() instanceof ElytraItem || CrystalAura.mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }

    private int findEmptySlot() {
        for (int i = 0; i < 9; ++i) {
            if (!CrystalAura.mc.player.inventory.getStackInSlot(i).isEmpty() || CrystalAura.mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }

    public static void moveItem(int from, int to, boolean air) {
        if (from != to) {
            CrystalAura.pickupItem(from, 0);
            CrystalAura.pickupItem(to, 0);
            if (air) {
                CrystalAura.pickupItem(from, 0);
            }
        }
    }

    public static void pickupItem(int slot, int button) {
        CrystalAura.mc.playerController.windowClick(0, slot, button, ClickType.PICKUP, CrystalAura.mc.player);
    }

    private void onUpdate(EventUpdate e) {
        if (this.obsidianPos != null) {
            this.findEnderCrystals(this.obsidianPos).forEach(this::attackCrystal);
        }
        if (this.crystalEntity != null && !this.crystalEntity.isAlive()) {
            this.reset();
        }
    }

    private void onMotion(EventSync e) {
        if (this.isValid(this.crystalEntity)) {
            Load.getInstance().getHooks().getModuleManagers().getAura().selfRotation = this.rotationVector = MathUtils.rotationToEntity(this.crystalEntity);
            Load.getInstance().getHooks().getModuleManagers().getAura().fakeRotation = this.rotationVector;
            e.setYaw(this.rotationVector.x);
            e.setPitch(this.rotationVector.y);
            CrystalAura.mc.player.renderYawOffset = this.rotationVector.x;
            CrystalAura.mc.player.rotationYawHead = this.rotationVector.x;
            CrystalAura.mc.player.rotationPitchHead = this.rotationVector.y;
        }
    }

    @Override
    public void onDisabled() {
        this.reset();
    }

    private void attackCrystal(Entity entity) {
        AutoSprint autoSprint = Load.getInstance().getHooks().getModuleManagers().getAutoSprint();
        if (this.isValid(entity) && CrystalAura.mc.player.getCooledAttackStrength(1.0f) >= 1.0f && this.attackStopWatch.hasTimeElapsed()) {
            this.crystalEntity = entity;
            this.attackStopWatch.setLastMS(500L);
            if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint() && CEntityActionPacket.lastUpdatedSprint) {
                CrystalAura.mc.player.connection.sendPacket(new CEntityActionPacket(CrystalAura.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            }
            CrystalAura.mc.playerController.attackEntity(CrystalAura.mc.player, entity);
            CrystalAura.mc.player.swingArm(Hand.MAIN_HAND);
            if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint()) {
                CrystalAura.mc.player.connection.sendPacket(new CEntityActionPacket(CrystalAura.mc.player, CEntityActionPacket.Action.START_SPRINTING));
            }
        }
        if (!entity.isAlive()) {
            this.reset();
        }
    }

    public static int getSlotInInventoryOrHotbar(Item item, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        int finalSlot = -1;
        for (int i = firstSlot; i < lastSlot; ++i) {
            if (CrystalAura.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            finalSlot = i;
        }
        return finalSlot;
    }

    private void setAndUseCrystal(int slot, BlockPos pos) {
        Hand hand;
        boolean isOffHand = CrystalAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        Vector3d center = new Vector3d((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f);
        if (!isOffHand) {
            CrystalAura.mc.player.inventory.currentItem = slot;
        }
        Hand hand2 = hand = isOffHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (CrystalAura.mc.playerController.processRightClickBlock(CrystalAura.mc.player, CrystalAura.mc.world, hand, new BlockRayTraceResult(center, Direction.UP, pos, false)) == ActionResultType.SUCCESS) {
            CrystalAura.mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    private boolean isValid(Entity base) {
        if (base == null) {
            return false;
        }
        if (this.obsidianPos == null) {
            return false;
        }
        for (Entity entity : CrystalAura.mc.world.getAllEntities()) {
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity entity1 = (ItemEntity)entity;
            if (!((Boolean)this.safeYourSelf.getValue()).booleanValue() || !(entity1.getPosY() > (double)this.obsidianPos.getY())) continue;
            return false;
        }
        return this.isCorrectDistance();
    }

    private boolean isCorrectDistance() {
        if (this.obsidianPos == null) {
            return false;
        }
        return CrystalAura.mc.player.getPositionVec().distanceTo(new Vector3d(this.obsidianPos.getX(), this.obsidianPos.getY(), this.obsidianPos.getZ())) <= (double)CrystalAura.mc.playerController.getBlockReachDistance();
    }

    public List<Entity> findEnderCrystals(BlockPos position) {
        return CrystalAura.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(position.getX(), position.getY(), position.getZ(), (double)position.getX() + 1.0, (double)position.getY() + 2.0, (double)position.getZ() + 1.0)).stream().filter(entity -> entity instanceof EnderCrystalEntity).collect(Collectors.toList());
    }

    private void reset() {
        this.crystalEntity = null;
        this.obsidianPos = null;
        this.rotationVector = null;
        this.oldCurrentSlot = -1;
        this.bestSlot = -1;
    }
}
