package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.events.ClientEvent;
import java.util.UUID;
import javax.annotation.Nullable;

public interface OpenALSoundEvent
extends ClientEvent {
    @Nullable
    public Position getPosition();

    @Nullable
    public UUID getChannelId();

    public int getSource();

    @Nullable
    public String getCategory();

    public static interface Post
    extends OpenALSoundEvent {
    }

    public static interface Pre
    extends OpenALSoundEvent {
    }
}
