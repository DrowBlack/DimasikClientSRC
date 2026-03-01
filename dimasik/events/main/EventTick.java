package dimasik.events.main;

import dimasik.events.api.main.Event;
import lombok.Generated;

public class EventTick
implements Event {
    private final Phase phase;

    @Generated
    public EventTick(Phase phase) {
        this.phase = phase;
    }

    @Generated
    public Phase getPhase() {
        return this.phase;
    }

    public static enum Phase {
        START,
        MID,
        END;

    }

    public static class Render
    extends EventTick {
        private final float renderTickTime;

        public Render(Phase phase, float renderTickTime) {
            super(phase);
            this.renderTickTime = renderTickTime;
        }

        @Generated
        public float getRenderTickTime() {
            return this.renderTickTime;
        }
    }
}
