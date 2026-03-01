package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class PlayerStatesPacket
implements Packet<PlayerStatesPacket> {
    private Map<UUID, PlayerState> playerStates;
    public static final ResourceLocation PLAYER_STATES = new ResourceLocation("voicechat", "player_states");

    public PlayerStatesPacket() {
    }

    public PlayerStatesPacket(Map<UUID, PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public Map<UUID, PlayerState> getPlayerStates() {
        return this.playerStates;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return PLAYER_STATES;
    }

    @Override
    public PlayerStatesPacket fromBytes(PacketBuffer buf) {
        this.playerStates = new HashMap<UUID, PlayerState>();
        int count = buf.readInt();
        for (int i = 0; i < count; ++i) {
            PlayerState playerState = PlayerState.fromBytes(buf);
            this.playerStates.put(playerState.getUuid(), playerState);
        }
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.playerStates.size());
        for (Map.Entry<UUID, PlayerState> entry : this.playerStates.entrySet()) {
            entry.getValue().toBytes(buf);
        }
    }
}
