package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.utils.client.ChatUtils;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTimePacket;

public class PacketDebug
extends Module {
    private final MultiOption options = new MultiOption("Options", new MultiOptionValue("Send", true), new MultiOptionValue("Receive", true));
    private final EventListener<EventSendPacket> send = this::send;
    private final EventListener<EventReceivePacket> receive = this::receive;

    public PacketDebug() {
        super("PacketDebug", Category.MISC);
        this.settings(this.options);
    }

    public void send(EventSendPacket eventSendPacket) {
        if (!(eventSendPacket.getPacket() instanceof CConfirmTransactionPacket) && !(eventSendPacket.getPacket() instanceof CPlayerPacket) && !(eventSendPacket.getPacket() instanceof CKeepAlivePacket) && this.options.getSelected("Send")) {
            ChatUtils.addMessage(eventSendPacket.getPacket().toString());
        }
    }

    public void receive(EventReceivePacket eventReceivePacket) {
        if (!(eventReceivePacket.getPacket() instanceof SEntityTeleportPacket || eventReceivePacket.getPacket() instanceof SConfirmTransactionPacket || eventReceivePacket.getPacket() instanceof STeamsPacket || eventReceivePacket.getPacket() instanceof SPlaySoundEventPacket || eventReceivePacket.getPacket() instanceof SEntityHeadLookPacket || eventReceivePacket.getPacket() instanceof SUpdateScorePacket || eventReceivePacket.getPacket() instanceof SKeepAlivePacket || eventReceivePacket.getPacket() instanceof SUpdateBossInfoPacket || eventReceivePacket.getPacket() instanceof SChatPacket || eventReceivePacket.getPacket() instanceof SPlaySoundPacket || eventReceivePacket.getPacket() instanceof SEntityPropertiesPacket || eventReceivePacket.getPacket() instanceof SPlaySoundEffectPacket || eventReceivePacket.getPacket() instanceof SPlayerListItemPacket || eventReceivePacket.getPacket() instanceof SUpdateTimePacket || eventReceivePacket.getPacket() instanceof SEntityMetadataPacket || eventReceivePacket.getPacket() instanceof SEntityVelocityPacket || eventReceivePacket.getPacket() instanceof SEntityPacket || eventReceivePacket.getPacket() instanceof SSpawnParticlePacket || !this.options.getSelected("Receive"))) {
            ChatUtils.addMessage(eventReceivePacket.getPacket().toString());
        }
    }
}
