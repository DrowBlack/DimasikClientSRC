package dimasik.managers.mods.voicechat.api.audiolistener;

import dimasik.managers.mods.voicechat.api.ServerPlayer;
import dimasik.managers.mods.voicechat.api.audiolistener.AudioListener;
import dimasik.managers.mods.voicechat.api.packets.SoundPacket;
import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerAudioListener
extends AudioListener {
    public UUID getPlayerUuid();

    public static interface Builder {
        public Builder setPlayer(ServerPlayer var1);

        public Builder setPlayer(UUID var1);

        public Builder setPacketListener(Consumer<SoundPacket> var1);

        public PlayerAudioListener build();
    }
}
