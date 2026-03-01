package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.Player;
import dimasik.managers.mods.voicechat.api.ServerLevel;

public interface ServerPlayer
extends Player {
    public ServerLevel getServerLevel();
}
