package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import java.util.Arrays;
import java.util.function.Supplier;

public class AudioSupplier
implements Supplier<short[]> {
    private final short[] audioData;
    private final short[] frame;
    private int framePosition;

    public AudioSupplier(short[] audioData) {
        this.audioData = audioData;
        this.frame = new short[960];
    }

    @Override
    public short[] get() {
        if (this.framePosition >= this.audioData.length) {
            return null;
        }
        Arrays.fill(this.frame, (short)0);
        System.arraycopy(this.audioData, this.framePosition, this.frame, 0, Math.min(this.frame.length, this.audioData.length - this.framePosition));
        this.framePosition += this.frame.length;
        return this.frame;
    }
}
