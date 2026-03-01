package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.api.Option;
import dimasik.utils.player.MoveUtils;

public class DragonFly
extends Module {
    private final EventListener<EventUpdate> update = this::eventmove;

    public DragonFly() {
        super("Dragon Fly", Category.MOVEMENT);
        this.settings(new Option[0]);
    }

    public void eventmove(EventUpdate event) {
        if (DragonFly.mc.player.abilities.isFlying) {
            MoveUtils.setMotion(1.05);
            DragonFly.mc.player.motion.y = 0.0;
            if (DragonFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                DragonFly.mc.player.motion.y = 0.15;
                if (DragonFly.mc.player.moveForward == 0.0f && !DragonFly.mc.gameSettings.keyBindLeft.isKeyDown() && !DragonFly.mc.gameSettings.keyBindRight.isKeyDown()) {
                    DragonFly.mc.player.motion.y = 1.0;
                }
            }
            if (DragonFly.mc.gameSettings.keyBindSneak.isKeyDown()) {
                DragonFly.mc.player.motion.y = -0.15;
                if (DragonFly.mc.player.moveForward == 0.0f && !DragonFly.mc.gameSettings.keyBindLeft.isKeyDown() && !DragonFly.mc.gameSettings.keyBindRight.isKeyDown()) {
                    DragonFly.mc.player.motion.y = -1.0;
                }
            }
        }
    }
}
