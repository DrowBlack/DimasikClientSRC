package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.Position;
import java.util.UUID;

public interface Entity {
    public UUID getUuid();

    public Object getEntity();

    public Position getPosition();
}
