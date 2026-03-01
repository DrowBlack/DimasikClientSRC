package dimasik.modules.combat;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.events.main.render.EventRender3D;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.modules.combat.Aura;
import dimasik.utils.time.TimerUtils;
import java.util.ArrayList;
import lombok.Generated;
import net.minecraft.entity.EntitySize;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class WallsBypass
extends Module {
    private final ArrayList<IPacket<?>> packets = new ArrayList();
    private Vector3d enablePosition = new Vector3d(0.0, 0.0, 0.0);
    private AxisAlignedBB renderAxis = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private final TimerUtils timer = new TimerUtils();
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Auto"), new SelectOptionValue("Manual"));
    private final EventListener<EventSendPacket> send = this::send;
    private final EventListener<EventReceivePacket> receive = this::receive;
    private final EventListener<EventRender3D.Post> render = this::render;
    private final EventListener<EventUpdate> update = this::update;

    public WallsBypass() {
        super("WallsBypass", Category.COMBAT);
        this.settings(this.mode);
    }

    public void update(EventUpdate event) {
        if (this.mode.getSelected("Manual")) {
            WallsBypass.mc.playerController.resetBlockRemoving();
            WallsBypass.mc.playerController.setBlockHitDelay(0);
            WallsBypass.mc.playerController.setCurBlockDamageMP(1.0f);
        }
    }

    public void render(EventRender3D.Post event) {
        if (this.mode.getSelected("Manual")) {
            VisualHelpers.drawBox(this.renderAxis.offset(-WallsBypass.mc.getRenderManager().info.getProjectedView().x, -WallsBypass.mc.getRenderManager().info.getProjectedView().y, -WallsBypass.mc.getRenderManager().info.getProjectedView().z), -1);
        }
    }

    public void send(EventSendPacket event) {
        if (this.mode.getSelected("Manual")) {
            Aura aura = Load.getInstance().getHooks().getModuleManagers().getAura();
            boolean canCancel = true;
            if (aura.getTarget() != null && (WallsBypass.mc.player.getPositionVec().distanceTo(aura.getTarget().getPositionVec()) > (double)((Float)aura.getDistance().getValue()).floatValue() || this.enablePosition.distanceTo(aura.getTarget().getPositionVec()) > (double)((Float)aura.getDistance().getValue()).floatValue()) || this.timer.hasTimeElapsed(30000L)) {
                this.enablePosition = WallsBypass.mc.player.getPositionVec();
                EntitySize entitysize = WallsBypass.mc.player.getSize(WallsBypass.mc.player.getPose());
                double d0 = (double)entitysize.width / 2.0;
                this.renderAxis = new AxisAlignedBB(WallsBypass.mc.player.getPosX() - d0, WallsBypass.mc.player.getPosY(), WallsBypass.mc.player.getPosZ() - d0, WallsBypass.mc.player.getPosX() + d0, WallsBypass.mc.player.getPosY() + (double)entitysize.height, WallsBypass.mc.player.getPosZ() + d0);
                canCancel = false;
                for (IPacket<?> packet : this.packets) {
                    WallsBypass.processPacket(packet, mc.getConnection().getNetworkManager().getNetHandler());
                }
                this.packets.clear();
                this.timer.reset();
            }
            if (event.getPacket() instanceof CConfirmTransactionPacket && canCancel) {
                event.setCancelled(true);
            }
        }
    }

    public void receive(EventReceivePacket event) {
        IPacket<?> iPacket;
        if (this.mode.getSelected("Manual") && (iPacket = event.getPacket()) instanceof SChangeBlockPacket) {
            SChangeBlockPacket packet = (SChangeBlockPacket)iPacket;
            this.packets.add(packet);
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisabled() {
        if (this.mode.getSelected("Manual")) {
            if (WallsBypass.mc.player != null) {
                for (IPacket<?> packet : this.packets) {
                    WallsBypass.processPacket(packet, mc.getConnection().getNetworkManager().getNetHandler());
                }
                this.packets.clear();
            }
            this.timer.reset();
        }
    }

    @Override
    public void onEnabled() {
        if (this.mode.getSelected("Manual")) {
            if (WallsBypass.mc.player != null) {
                this.enablePosition = WallsBypass.mc.player.getPositionVec();
                EntitySize entitysize = WallsBypass.mc.player.getSize(WallsBypass.mc.player.getPose());
                double d0 = (double)entitysize.width / 2.0;
                this.renderAxis = new AxisAlignedBB(WallsBypass.mc.player.getPosX() - d0, WallsBypass.mc.player.getPosY(), WallsBypass.mc.player.getPosZ() - d0, WallsBypass.mc.player.getPosX() + d0, WallsBypass.mc.player.getPosY() + (double)entitysize.height, WallsBypass.mc.player.getPosZ() + d0);
            }
            this.timer.reset();
        }
    }

    private static <T extends INetHandler> void processPacket(IPacket<T> p_197664_0_, INetHandler p_197664_1_) {
        p_197664_0_.processPacket(p_197664_1_);
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }
}
