package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.events.ClientEvent;
import java.util.UUID;
import javax.annotation.Nullable;

public interface ClientReceiveSoundEvent
extends ClientEvent {
    public UUID getId();

    public short[] getRawAudio();

    public void setRawAudio(@Nullable short[] var1);

    public static interface StaticSound
    extends ClientReceiveSoundEvent {
    }

    public static interface LocationalSound
    extends ClientReceiveSoundEvent {
        public Position getPosition();

        public float getDistance();
    }

    public static interface EntitySound
    extends ClientReceiveSoundEvent {
        public boolean isWhispering();

        public float getDistance();
    }
}
