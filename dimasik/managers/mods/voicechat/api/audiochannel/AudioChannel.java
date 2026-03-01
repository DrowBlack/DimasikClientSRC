package dimasik.managers.mods.voicechat.api.audiochannel;

import dimasik.managers.mods.voicechat.api.ServerPlayer;
import dimasik.managers.mods.voicechat.api.packets.MicrophonePacket;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public interface AudioChannel {
    public void send(byte[] var1);

    public void send(MicrophonePacket var1);

    public void setFilter(Predicate<ServerPlayer> var1);

    public void flush();

    public boolean isClosed();

    public UUID getId();

    @Nullable
    public String getCategory();

    public void setCategory(@Nullable String var1);
}
