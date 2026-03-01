package dimasik.modules.combat;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventMoveInput;
import dimasik.events.main.player.EventSync;
import dimasik.helpers.module.aura.AuraHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.time.TimerUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class AntiCrystal
extends Module {
    private final TimerUtils timer = new TimerUtils();
    private final long delayMs = 100L;
    private Vector2f rotation = new Vector2f(0.0f, 0.0f);
    private BlockPos obs = null;
    private boolean plac = false;
    private final AuraHelpers auraHelpers = new AuraHelpers();
    private final EventListener<EventUpdate> update = this::onUpdate;
    private final EventListener<EventSync> sync = this::onSync;
    private final EventListener<EventMoveInput> input = this::input;

    public AntiCrystal() {
        super("AntiCrystal", Category.COMBAT);
    }

    @Override
    public void onEnabled() {
        this.reset();
    }

    @Override
    public void onDisabled() {
        this.reset();
    }

    private void reset() {
        this.rotation = new Vector2f(AntiCrystal.mc.player.rotationYaw, AntiCrystal.mc.player.rotationPitch);
        this.obs = null;
        this.plac = false;
    }

    public void input(EventMoveInput eventMoveInput) {
        if (this.obs != null && this.findblock() != -1) {
            this.auraHelpers.fixMovement(eventMoveInput, this.rotation.x);
        }
    }

    public void onUpdate(EventUpdate event) {
        if (AntiCrystal.mc.player == null || AntiCrystal.mc.world == null) {
            this.reset();
            return;
        }
        if (!this.timer.hasTimeElapsed(100L)) {
            return;
        }
        this.plac = false;
        this.obs = null;
        BlockPos playerFeetPos = AntiCrystal.mc.player.getPosition();
        if (AntiCrystal.mc.world.getBlockState(playerFeetPos.down()).getBlock() == Blocks.OBSIDIAN) {
            return;
        }
        if (this.obs == null && this.findblock() == -1) {
            this.rotation = new Vector2f(AntiCrystal.mc.player.rotationYaw, AntiCrystal.mc.player.rotationPitch);
        }
        int checkRadius = 0;
        for (int dx = -checkRadius; dx <= checkRadius; ++dx) {
            for (int dz = -checkRadius; dz <= checkRadius; ++dz) {
                BlockPos checkPos = playerFeetPos.add(dx, 0, dz);
                if (AntiCrystal.mc.world.getBlockState(checkPos.down()).getBlock() != Blocks.OBSIDIAN || AntiCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(checkPos)).size() <= 0) continue;
                this.rotation = new Vector2f(AntiCrystal.mc.player.rotationYaw, AntiCrystal.mc.player.rotationPitch);
            }
        }
        int radius = 3;
        BlockPos ply = AntiCrystal.mc.player.getPosition();
        boolean obs1 = false;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -2; dy <= 2; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    BlockPos pos = ply.add(dx, dy, dz);
                    if (AntiCrystal.mc.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN || !AntiCrystal.mc.world.isAirBlock(pos.up())) continue;
                    obs1 = true;
                    int slotb = this.findblock();
                    if (slotb == -1) continue;
                    Vector3d blockc = new Vector3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
                    Vector3d playere = AntiCrystal.mc.player.getEyePosition(1.0f);
                    double dxRot = blockc.x - playere.x;
                    double dyRot = blockc.y - playere.y;
                    double dzRot = blockc.z - playere.z;
                    double dist = Math.sqrt(dxRot * dxRot + dzRot * dzRot);
                    float yaw = (float)Math.toDegrees(Math.atan2(dzRot, dxRot)) - 90.0f;
                    float pitch = (float)(-Math.toDegrees(Math.atan2(dyRot, dist)));
                    this.rotation = new Vector2f(yaw, pitch);
                    this.obs = pos;
                    this.plac = true;
                    this.placebl();
                    return;
                }
            }
        }
        if (!obs1) {
            this.rotation = new Vector2f(AntiCrystal.mc.player.rotationYaw, AntiCrystal.mc.player.rotationPitch);
        }
    }

    public void onSync(EventSync event) {
        if (this.rotation != null && this.obs != null && this.findblock() != -1) {
            event.setYaw(this.rotation.x);
            event.setPitch(this.rotation.y);
            AntiCrystal.mc.player.rotationYawHead = this.rotation.x;
            AntiCrystal.mc.player.renderYawOffset = this.rotation.x;
            AntiCrystal.mc.player.rotationPitchHead = this.rotation.y;
        }
    }

    private void placebl() {
        if (this.obs == null) {
            return;
        }
        int slotb = this.findblock();
        if (slotb != -1) {
            Vector3d hitVec;
            BlockRayTraceResult result;
            ActionResultType action;
            int prevSlot = AntiCrystal.mc.player.inventory.currentItem;
            if (prevSlot != slotb) {
                AntiCrystal.mc.player.inventory.currentItem = slotb;
                AntiCrystal.mc.player.connection.sendPacket(new CHeldItemChangePacket(slotb));
            }
            if ((action = AntiCrystal.mc.playerController.processRightClickBlock(AntiCrystal.mc.player, AntiCrystal.mc.world, Hand.MAIN_HAND, result = new BlockRayTraceResult(hitVec = new Vector3d((double)this.obs.getX() + 0.5, this.obs.getY() + 1, (double)this.obs.getZ() + 0.5), Direction.UP, this.obs, false))) == ActionResultType.SUCCESS) {
                AntiCrystal.mc.player.swingArm(Hand.MAIN_HAND);
                this.timer.setLastMS(100L);
            }
            if (prevSlot != slotb) {
                AntiCrystal.mc.player.inventory.currentItem = prevSlot;
                AntiCrystal.mc.player.connection.sendPacket(new CHeldItemChangePacket(prevSlot));
            }
        }
        this.plac = false;
        this.obs = null;
    }

    private int findblock() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = AntiCrystal.mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem) || stack.getItem() == Items.OBSIDIAN || stack.getItem() == Items.AIR) continue;
            return i;
        }
        return -1;
    }
}
