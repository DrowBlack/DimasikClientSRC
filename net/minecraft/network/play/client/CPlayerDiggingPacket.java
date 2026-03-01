package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class CPlayerDiggingPacket
implements IPacket<IServerPlayNetHandler> {
    private BlockPos position;
    private Direction facing;
    private Action action;

    public CPlayerDiggingPacket() {
    }

    public CPlayerDiggingPacket(Action actionIn, BlockPos posIn, Direction facingIn) {
        this.action = actionIn;
        this.position = posIn.toImmutable();
        this.facing = facingIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.action = buf.readEnumValue(Action.class);
        this.position = buf.readBlockPos();
        this.facing = Direction.byIndex(buf.readUnsignedByte());
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.action);
        buf.writeBlockPos(this.position);
        buf.writeByte(this.facing.getIndex());
    }

    @Override
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processPlayerDigging(this);
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_ITEM_WITH_OFFHAND;

    }
}
