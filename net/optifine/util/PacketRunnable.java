package net.optifine.util;

import net.minecraft.network.IPacket;

public class PacketRunnable
implements Runnable {
    private IPacket packet;
    private Runnable runnable;

    public PacketRunnable(IPacket packet, Runnable runnable) {
        this.packet = packet;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        this.runnable.run();
    }

    public IPacket getPacket() {
        return this.packet;
    }

    public String toString() {
        return "PacketRunnable: " + String.valueOf(this.packet);
    }
}
