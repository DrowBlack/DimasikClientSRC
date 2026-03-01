package dimasik.managers.mods.voicechat.events;

import dimasik.events.api.main.Event;
import net.minecraft.entity.player.ServerPlayerEntity;

public class VoiceChatCompatibilityCheckSucceededEvent
implements Event {
    private final ServerPlayerEntity player;

    public VoiceChatCompatibilityCheckSucceededEvent(ServerPlayerEntity player) {
        this.player = player;
    }

    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    public boolean isCancel() {
        return false;
    }
}
