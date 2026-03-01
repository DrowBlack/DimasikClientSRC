package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventMoveInput;
import dimasik.events.main.movement.EventMove;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.events.main.player.EventSync;
import dimasik.itemics.api.utils.RayTraceUtils;
import dimasik.itemics.api.utils.Rotation;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.player.MoveUtils;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class BlockFly
extends Module {
    private BlockCache blockCache;
    private BlockCache lastBlockCache;
    public Vector2f rotation;
    private float savedY;
    public static final ConcurrentLinkedQueue<TimedPacket> packets = new ConcurrentLinkedQueue();
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventMoveInput> input = this::input;
    private final EventListener<EventMove> move = this::move;
    private final EventListener<EventSendPacket> sendPacket = this::send;
    private final EventListener<EventSync> sync = this::sync;
    private final EventListener<EventSync> event = this::event;
    public boolean sneak;

    public BlockFly() {
        super("BlockFly", Category.MOVEMENT);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        for (TimedPacket p : packets) {
            BlockFly.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(p.getPacket());
        }
        packets.clear();
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        if (BlockFly.mc.player != null) {
            this.savedY = (float)BlockFly.mc.player.getPosY();
        }
    }

    public void send(EventSendPacket eventSendPacket) {
        CEntityActionPacket p;
        if (BlockFly.mc.player != null && BlockFly.mc.world != null && !mc.isSingleplayer() && !BlockFly.mc.player.getShouldBeDead()) {
            IPacket packet = eventSendPacket.getPacket();
            packets.add(new TimedPacket(packet, System.currentTimeMillis()));
            eventSendPacket.setCancelled(true);
        } else {
            this.toggle();
        }
        IPacket iPacket = eventSendPacket.getPacket();
        if (iPacket instanceof CEntityActionPacket && ((p = (CEntityActionPacket)iPacket).getAction() == CEntityActionPacket.Action.START_SPRINTING || p.getAction() == CEntityActionPacket.Action.STOP_SPRINTING)) {
            eventSendPacket.setCancelled(true);
        }
    }

    public void sync(EventSync eventMoveInput) {
        for (TimedPacket timedPacket : packets) {
            if (System.currentTimeMillis() - timedPacket.getTime() < 1000L) continue;
            BlockFly.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(timedPacket.getPacket());
            packets.remove(timedPacket);
        }
    }

    public void input(EventMoveInput e) {
        RayTraceResult result;
        if (MoveUtils.isMoving()) {
            e.setJump(false);
        }
        e.setSneak(false);
        if (!BlockFly.mc.player.isOnGround()) {
            e.setForward(0.0f);
            e.setStrafe(0.0f);
        }
        if (this.rotation != null && (result = RayTraceUtils.rayTraceTowards(BlockFly.mc.player, new Rotation(this.rotation.x, this.rotation.y), 3.0)).getType() != RayTraceResult.Type.BLOCK && BlockFly.mc.world.getBlockState(BlockFly.mc.player.getPosition().add(0.0, -0.5, 0.0)).getBlock() == Blocks.AIR) {
            e.setSneak(true);
        }
    }

    public void move(EventMove e) {
    }

    public void event(EventSync eventInput) {
        if (BlockFly.mc.player.isOnGround()) {
            this.savedY = (float)Math.floor(BlockFly.mc.player.getPosY() - 1.0);
        }
        this.blockCache = this.getBlockInfo();
        if (this.blockCache == null) {
            return;
        }
        this.lastBlockCache = this.getBlockInfo();
        if (BlockFly.mc.world.getBlockState(BlockFly.mc.player.getPosition().add(0.0, -0.5, 0.0)).getBlock() == Blocks.AIR) {
            float[] rot = this.getRotations(this.blockCache.position, this.blockCache.facing);
            this.rotation = new Vector2f(rot[0], rot[1]);
            eventInput.setYaw(this.rotation.x);
            eventInput.setPitch(this.rotation.y);
            BlockFly.mc.player.rotationPitchHead = this.rotation.y;
            BlockFly.mc.player.rotationYawHead = this.rotation.x;
        }
    }

    public void update(EventUpdate e) {
        if (this.blockCache == null || this.lastBlockCache == null) {
            return;
        }
        int block = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack s = BlockFly.mc.player.inventory.getStackInSlot(i);
            if (!(s.getItem() instanceof BlockItem)) continue;
            block = i;
            break;
        }
        if (block == -1) {
            this.toggle();
            return;
        }
        if (this.rotation == null) {
            return;
        }
        RayTraceResult result = RayTraceUtils.rayTraceTowards(BlockFly.mc.player, new Rotation(this.rotation.x, this.rotation.y), 3.0);
        if (BlockFly.mc.world.getBlockState(BlockFly.mc.player.getPosition().add(0.0, -0.5, 0.0)).getBlock() == Blocks.AIR && result.getType() == RayTraceResult.Type.BLOCK) {
            int last = BlockFly.mc.player.inventory.currentItem;
            BlockFly.mc.player.inventory.currentItem = block;
            BlockFly.mc.playerController.processRightClickBlock(BlockFly.mc.player, BlockFly.mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(this.getVector(this.lastBlockCache), this.lastBlockCache.getFacing(), this.lastBlockCache.getPosition(), false));
            BlockFly.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
            this.blockCache = null;
            BlockFly.mc.player.inventory.currentItem = last;
        }
    }

    public float[] getRotations(BlockPos blockPos, Direction enumFacing) {
        double d = (double)blockPos.getX() + 0.5 - BlockFly.mc.player.getPosX() + (double)enumFacing.getXOffset() * 0.25;
        double d2 = (double)blockPos.getZ() + 0.5 - BlockFly.mc.player.getPosZ() + (double)enumFacing.getZOffset() * 0.25;
        double d3 = BlockFly.mc.player.getPosY() + (double)BlockFly.mc.player.getEyeHeight() - (double)blockPos.getY() - (double)enumFacing.getYOffset() * 0.25;
        double d4 = MathHelper.sqrt(d * d + d2 * d2);
        float f = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(Math.atan2(d3, d4) * 180.0 / Math.PI);
        return new float[]{MathHelper.wrapDegrees(f), f2};
    }

    public BlockCache getBlockInfo() {
        int y = (int)(BlockFly.mc.player.getPosY() - 1.0 >= (double)this.savedY && Math.max(BlockFly.mc.player.getPosY(), (double)this.savedY) - Math.min(BlockFly.mc.player.getPosY(), (double)this.savedY) <= 3.0 && !BlockFly.mc.gameSettings.keyBindJump.isKeyDown() ? (double)this.savedY : BlockFly.mc.player.getPosY() - 1.0);
        BlockPos belowBlockPos = new BlockPos(BlockFly.mc.player.getPosX(), (double)(y - (BlockFly.mc.player.isSneaking() ? -1 : 0)), BlockFly.mc.player.getPosZ());
        if (BlockFly.mc.world.getBlockState(belowBlockPos).getBlock() instanceof AirBlock) {
            for (int x = 0; x < 3; ++x) {
                for (int z = 0; z < 3; ++z) {
                    for (int i = -1; i < 1; ++i) {
                        BlockPos blockPos = belowBlockPos.add(x * i, 0, z * i);
                        if (!(BlockFly.mc.world.getBlockState(blockPos).getBlock() instanceof AirBlock)) continue;
                        for (Direction direction : Direction.values()) {
                            BlockPos block = blockPos.offset(direction);
                            Material material = BlockFly.mc.world.getBlockState(block).getBlock().getDefaultState().getMaterial();
                            if (!material.isSolid() || material.isLiquid()) continue;
                            return new BlockCache(block, direction.getOpposite());
                        }
                    }
                }
            }
        }
        return null;
    }

    public Vector3d getVector(BlockCache data) {
        BlockPos pos = data.position;
        Direction face = data.facing;
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.5;
        double z = (double)pos.getZ() + 0.5;
        if (face != Direction.UP && face != Direction.DOWN) {
            y += 0.5;
        } else {
            x += 0.3;
            z += 0.3;
        }
        if (face == Direction.WEST || face == Direction.EAST) {
            z += 0.15;
        }
        if (face == Direction.SOUTH || face == Direction.NORTH) {
            x += 0.15;
        }
        return new Vector3d(x, y, z);
    }

    public static class TimedPacket {
        private final IPacket<?> packet;
        private final long time;

        public TimedPacket(IPacket<?> packet, long time) {
            this.packet = packet;
            this.time = time;
        }

        public IPacket<?> getPacket() {
            return this.packet;
        }

        public long getTime() {
            return this.time;
        }
    }

    public static class BlockCache {
        private final BlockPos position;
        private final Direction facing;

        public BlockCache(BlockPos position, Direction facing) {
            this.position = position;
            this.facing = facing;
        }

        @Generated
        public BlockPos getPosition() {
            return this.position;
        }

        @Generated
        public Direction getFacing() {
            return this.facing;
        }
    }
}
