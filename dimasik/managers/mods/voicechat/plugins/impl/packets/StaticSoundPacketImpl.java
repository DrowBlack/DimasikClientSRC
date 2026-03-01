package dimasik.managers.mods.voicechat.plugins.impl.packets;

import dimasik.managers.mods.voicechat.api.packets.StaticSoundPacket;
import dimasik.managers.mods.voicechat.plugins.impl.packets.SoundPacketImpl;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import java.util.UUID;
import javax.annotation.Nullable;

public class StaticSoundPacketImpl
extends SoundPacketImpl
implements StaticSoundPacket {
    public StaticSoundPacketImpl(GroupSoundPacket packet) {
        super(packet);
    }

    public static class BuilderImpl
    extends SoundPacketImpl.BuilderImpl<BuilderImpl, StaticSoundPacket>
    implements StaticSoundPacket.Builder<BuilderImpl> {
        public BuilderImpl(SoundPacketImpl soundPacket) {
            super(soundPacket);
        }

        public BuilderImpl(UUID channelId, UUID sender, byte[] opusEncodedData, long sequenceNumber, @Nullable String category) {
            super(channelId, sender, opusEncodedData, sequenceNumber, category);
        }

        @Override
        public StaticSoundPacket build() {
            return new StaticSoundPacketImpl(new GroupSoundPacket(this.channelId, this.sender, this.opusEncodedData, this.sequenceNumber, this.category));
        }
    }
}
