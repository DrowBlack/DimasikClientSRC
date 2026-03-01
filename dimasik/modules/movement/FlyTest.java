package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.player.EventSync;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.utils.math.MathUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.util.math.vector.Vector3d;

public class FlyTest
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Elytra Glide"));
    private final TimerUtils ticks = new TimerUtils();
    private int ticksTwo = 0;
    private final EventListener<EventSync> sync = this::sync;

    public FlyTest() {
        super("Fly", Category.MOVEMENT);
        this.settings(this.mode);
    }

    public void sync(EventSync event) {
        if (this.mode.getSelected("Elytra Glide")) {
            float valuePidor;
            if (FlyTest.mc.player == null || FlyTest.mc.world == null || !FlyTest.mc.player.isElytraFlying()) {
                return;
            }
            ++this.ticksTwo;
            Vector3d pos = FlyTest.mc.player.getPositionVec();
            float yaw = FlyTest.mc.player.rotationYaw;
            double forward = 0.087;
            double motion = MathUtils.getBps(FlyTest.mc.player, 1);
            float f = valuePidor = ClientManagers.isReallyWorld() ? 40.0f : 52.0f;
            if (motion >= (double)valuePidor) {
                forward = 0.0;
                motion = 0.0;
            }
            double dx = -Math.sin(Math.toRadians(yaw)) * forward;
            double dz = Math.cos(Math.toRadians(yaw)) * forward;
            FlyTest.mc.player.setVelocity(dx * (double)MathUtils.random1(1.1f, 1.21f), FlyTest.mc.player.getMotion().y - (double)0.02f, dz * (double)MathUtils.random(1.1f, 1.21f));
            FlyTest.mc.player.setPosition(pos.getX() + dx, pos.getY(), pos.getZ() + dz);
            FlyTest.mc.player.setVelocity(dx * (double)MathUtils.random1(1.1f, 1.21f), FlyTest.mc.player.getMotion().y + (double)0.016f, dz * (double)MathUtils.random(1.1f, 1.21f));
        }
    }
}
