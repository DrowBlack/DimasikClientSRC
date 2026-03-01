package dimasik.modules.render;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventTick;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.visual.EventFog;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.vector.Vector3d;

public class CustomWorld
extends Module {
    private final MultiOption mode = new MultiOption("Mode", new MultiOptionValue("Time", true), new MultiOptionValue("Fog", true));
    private final SelectOption time = new SelectOption("Time", 0, new SelectOptionValue("Morning"), new SelectOptionValue("Day"), new SelectOptionValue("Evening"), new SelectOptionValue("Night")).visible(() -> this.mode.getSelected("Time"));
    private final SliderOption distance = new SliderOption("Distance Fog", 4.0f, 1.1f, 30.0f).increment(0.1f).visible(() -> this.mode.getSelected("Fog"));
    private final EventListener<EventReceivePacket> receive = this::packet;
    private final EventListener<EventFog> fog = this::fog;
    private final EventListener<EventTick> tick = this::update;

    public CustomWorld() {
        super("Custom World", Category.RENDER);
        this.settings(this.mode, this.time, this.distance);
    }

    public void packet(EventReceivePacket event) {
        if (event.getPacket() instanceof SUpdateTimePacket && this.mode.getSelected("Time")) {
            event.setCancelled(true);
        }
    }

    public void fog(EventFog event) {
        if (this.mode.getSelected("Fog")) {
            float[] color = ColorHelpers.getRGBAf(ColorHelpers.getThemeColor(2));
            Vector3d colors = new Vector3d(color[0], color[1], color[2]);
            event.setColor(colors);
            float fogDistance = 1.0f / ((Float)this.distance.getValue()).floatValue();
            event.setDistance(fogDistance);
        }
    }

    public void update(EventTick event) {
        if (this.mode.getSelected("Time") && CustomWorld.mc.world != null) {
            if (this.time.getSelected("Morning")) {
                CustomWorld.mc.world.setDayTime(23000L);
            } else if (this.time.getSelected("Day")) {
                CustomWorld.mc.world.setDayTime(6000L);
            } else if (this.time.getSelected("Evening")) {
                CustomWorld.mc.world.setDayTime(12000L);
            } else if (this.time.getSelected("Night")) {
                CustomWorld.mc.world.setDayTime(18000L);
            }
        }
    }
}
