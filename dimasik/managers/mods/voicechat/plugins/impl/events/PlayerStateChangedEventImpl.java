package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.PlayerStateChangedEvent;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatConnectionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PlayerStateChangedEventImpl
extends ServerEventImpl
implements PlayerStateChangedEvent {
    protected final PlayerState state;
    @Nullable
    protected VoicechatConnectionImpl connection;

    public PlayerStateChangedEventImpl(PlayerState state) {
        this.state = state;
    }

    @Override
    public boolean isDisabled() {
        return this.state.isDisabled();
    }

    @Override
    public boolean isDisconnected() {
        return this.state.isDisconnected();
    }

    @Override
    public UUID getPlayerUuid() {
        return this.state.getUuid();
    }

    @Override
    @Nullable
    public VoicechatConnection getConnection() {
        if (this.connection == null) {
            Server server = Voicechat.SERVER.getServer();
            if (server == null) {
                return null;
            }
            ServerPlayerEntity player = server.getServer().getPlayerList().getPlayerByUUID(this.state.getUuid());
            if (player == null) {
                return null;
            }
            this.connection = VoicechatConnectionImpl.fromPlayer(player);
        }
        return this.connection;
    }
}
