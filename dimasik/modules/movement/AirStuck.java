package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.events.main.player.EventSync;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import net.minecraft.item.BlockItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;

public class AirStuck
extends Module {
    private final EventListener<EventSendPacket> send = this::onpacket;
    private final EventListener<EventSync> sync = this::onMotion;

    public AirStuck() {
        super("AirStuck", Category.MOVEMENT);
    }

    public void onpacket(EventSendPacket e) {
        IPacket var3;
        AirStuck.mc.player.setVelocity(0.0, 0.0, 0.0);
        if (e.getPacket() instanceof CUseEntityPacket && ((CUseEntityPacket)e.getPacket()).getEntityFromWorld(AirStuck.mc.world).getEntityId() == AirStuck.mc.player.getEntityId()) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof CPlayerTryUseItemOnBlockPacket && !(AirStuck.mc.player.inventory.getCurrentItem().getItem() instanceof BlockItem)) {
            e.setCancelled(true);
        }
        if ((var3 = e.getPacket()) instanceof CPlayerPacket) {
            CPlayerPacket p = (CPlayerPacket)var3;
            if (AirStuck.mc.player != null) {
                if (p.isMoving()) {
                    p.setX(AirStuck.mc.player.getPosX());
                    p.setY(AirStuck.mc.player.getPosY());
                    p.setZ(AirStuck.mc.player.getPosZ());
                }
                p.setOnGround(AirStuck.mc.player.isOnGround());
                if (p.isRotating()) {
                    p.setYaw(AirStuck.mc.player.rotationYaw);
                    p.setPitch(AirStuck.mc.player.rotationPitch);
                }
            }
            if (AirStuck.mc.player == null) {
                this.toggle();
            }
        }
    }

    public void onMotion(EventSync eventMotion) {
        AirStuck.mc.player.setVelocity(0.0, 0.0, 0.0);
        eventMotion.setCancelled(true);
    }
}
