package dimasik.modules.player;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CUseEntityPacket;

public class NoFriendDamage
extends Module {
    private final EventListener<EventSendPacket> send = this::packet;

    public NoFriendDamage() {
        super("NoFriendDamage", Category.PLAYER);
    }

    /*
     * Unable to fully structure code
     */
    public void packet(EventSendPacket event) {
        block3: {
            var3_2 = event.getPacket();
            if (!(var3_2 instanceof CUseEntityPacket)) break block3;
            packet = (CUseEntityPacket)var3_2;
            entity = packet.getEntityFromWorld(NoFriendDamage.mc.world);
            if (!(entity instanceof PlayerEntity)) ** GOTO lbl-1000
            player = (PlayerEntity)entity;
            if (Load.getInstance().getHooks().getFriendManagers().is(player.getGameProfile().getName())) {
                v0 = true;
            } else lbl-1000:
            // 2 sources

            {
                v0 = false;
            }
            flag = v0;
            v1 = flag2 = packet.getAction() == CUseEntityPacket.Action.ATTACK;
            if (entity instanceof RemoteClientPlayerEntity && flag && flag2) {
                event.setCancelled(true);
            }
        }
    }
}
