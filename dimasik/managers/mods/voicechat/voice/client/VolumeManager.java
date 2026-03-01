package dimasik.managers.mods.voicechat.voice.client;

import java.util.Arrays;

public class VolumeManager {
    private static final short MAX_AMPLIFICATION = 32766;
    private final float[] maxVolumes = new float[50];
    private int index;

    public VolumeManager() {
        Arrays.fill(this.maxVolumes, -1.0f);
    }

    public short[] adjustVolumeMono(short[] audio, float volume) {
        this.maxVolumes[this.index] = VolumeManager.getMaximumMultiplier(audio, volume);
        this.index = (this.index + 1) % this.maxVolumes.length;
        float min = -1.0f;
        for (float mul : this.maxVolumes) {
            if (mul < 0.0f) continue;
            if (min < 0.0f) {
                min = mul;
                continue;
            }
            if (!(mul < min)) continue;
            min = mul;
        }
        float maxVolume = Math.min(min, volume);
        for (int i = 0; i < audio.length; ++i) {
            audio[i] = (short)((float)audio[i] * maxVolume);
        }
        return audio;
    }

    private static float getMaximumMultiplier(short[] audio, float multiplier) {
        short max = 0;
        for (short value : audio) {
            short abs = value <= Short.MIN_VALUE ? (short)Math.abs(value + 1) : (short)Math.abs(value);
            if (abs <= max) continue;
            max = abs;
        }
        return Math.min(multiplier, 32766.0f / (float)max);
    }
}
