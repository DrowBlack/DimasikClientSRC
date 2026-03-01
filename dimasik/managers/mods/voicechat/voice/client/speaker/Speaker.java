package dimasik.managers.mods.voicechat.voice.client.speaker;

import dimasik.managers.mods.voicechat.voice.client.speaker.SpeakerException;
import javax.annotation.Nullable;
import net.minecraft.util.math.vector.Vector3d;

public interface Speaker {
    public void open() throws SpeakerException;

    public void play(short[] var1, float var2, @Nullable Vector3d var3, @Nullable String var4, float var5);

    default public void play(short[] data, float volume, @Nullable String category) {
        this.play(data, volume, null, category, 0.0f);
    }

    public void close();
}
