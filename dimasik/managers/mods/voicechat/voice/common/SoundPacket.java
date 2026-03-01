package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.voice.common.Packet;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class SoundPacket<T extends SoundPacket>
implements Packet<T> {
    public static final byte WHISPER_MASK = 1;
    public static final byte HAS_CATEGORY_MASK = 2;
    protected UUID channelId;
    protected UUID sender;
    protected byte[] data;
    protected long sequenceNumber;
    @Nullable
    protected String category;

    public SoundPacket(UUID channelId, UUID sender, byte[] data, long sequenceNumber, @Nullable String category) {
        this.channelId = channelId;
        this.sender = sender;
        this.data = data;
        this.sequenceNumber = sequenceNumber;
        this.category = category;
    }

    public SoundPacket(UUID channelId, UUID sender, short[] data, @Nullable String category) {
        this.channelId = channelId;
        this.sender = sender;
        this.data = Utils.shortsToBytes(data);
        this.sequenceNumber = -1L;
        this.category = category;
    }

    public SoundPacket() {
    }

    public UUID getChannelId() {
        return this.channelId;
    }

    public UUID getSender() {
        return this.sender;
    }

    public byte[] getData() {
        return this.data;
    }

    public boolean isFromClientAudioChannel() {
        return this.sequenceNumber < 0L;
    }

    public long getSequenceNumber() {
        return this.sequenceNumber;
    }

    @Nullable
    public String getCategory() {
        return this.category;
    }

    protected boolean hasFlag(byte data, byte mask) {
        return (data & mask) != 0;
    }

    protected byte setFlag(byte data, byte mask) {
        return (byte)(data | mask);
    }
}
