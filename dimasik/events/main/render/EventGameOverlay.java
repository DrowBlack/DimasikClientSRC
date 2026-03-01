package dimasik.events.main.render;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventGameOverlay
extends EventCancellable
implements Event {
    private final OverlayType overlayType;

    @Generated
    public OverlayType getOverlayType() {
        return this.overlayType;
    }

    @Generated
    public EventGameOverlay(OverlayType overlayType) {
        this.overlayType = overlayType;
    }

    public static enum OverlayType {
        Hurt,
        PumpkinOverlay,
        TotemPop,
        CameraBounds,
        Fire,
        Light,
        BossBar,
        Fog,
        WaterFog,
        LavaFog,
        Blindness,
        Scoreboard,
        Block,
        Nausea,
        Hologram;

    }
}
