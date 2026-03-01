package net.minecraft.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.util.CooldownTracker;

public class ServerCooldownTracker
extends CooldownTracker {
    private final ServerPlayerEntity player;

    public ServerCooldownTracker(ServerPlayerEntity playerIn) {
        this.player = playerIn;
    }

    @Override
    protected void notifyOnSet(Item itemIn, int ticksIn) {
        super.notifyOnSet(itemIn, ticksIn);
        this.player.connection.sendPacket(new SCooldownPacket(itemIn, ticksIn));
    }

    @Override
    protected void notifyOnRemove(Item itemIn) {
        super.notifyOnRemove(itemIn);
        this.player.connection.sendPacket(new SCooldownPacket(itemIn, 0));
    }
}
