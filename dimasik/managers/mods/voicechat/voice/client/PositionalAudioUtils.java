package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.voice.client.speaker.AudioType;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class PositionalAudioUtils {
    private static final Minecraft mc = Minecraft.getInstance();

    private static float[] getStereoVolume(Vector3d cameraPos, float yRot, Vector3d soundPos) {
        float rot;
        Vector3d d = soundPos.subtract(cameraPos).normalize();
        Vector2f diff = new Vector2f((float)d.x, (float)d.z);
        float diffAngle = Utils.angle(diff, new Vector2f(-1.0f, 0.0f));
        float angle = Utils.normalizeAngle(diffAngle - yRot % 360.0f);
        float dif = (float)(Math.abs(cameraPos.y - soundPos.y) / 32.0);
        float perc = rot = angle / 180.0f;
        if (rot < -0.5f) {
            perc = -(0.5f + (rot + 0.5f));
        } else if (rot > 0.5f) {
            perc = 0.5f - (rot - 0.5f);
        }
        float minVolume = 0.3f;
        float left = perc < 0.0f ? Math.abs((perc *= 1.0f - dif) * 1.4f) + minVolume : minVolume;
        float right = perc >= 0.0f ? perc * 1.4f + minVolume : minVolume;
        float fill = 1.0f - Math.max(left, right);
        return new float[]{left += fill, right += fill};
    }

    private static float[] getStereoVolume(Vector3d soundPos) {
        ActiveRenderInfo mainCamera = PositionalAudioUtils.mc.gameRenderer.getActiveRenderInfo();
        return PositionalAudioUtils.getStereoVolume(mainCamera.getProjectedView(), mainCamera.getYaw(), soundPos);
    }

    public static float getDistanceVolume(float maxDistance, Vector3d pos) {
        return PositionalAudioUtils.getDistanceVolume(maxDistance, PositionalAudioUtils.mc.gameRenderer.getActiveRenderInfo().getProjectedView(), pos);
    }

    public static float getDistanceVolume(float maxDistance, Vector3d listenerPos, Vector3d pos) {
        float distance = (float)pos.distanceTo(listenerPos);
        distance = Math.min(distance, maxDistance);
        return 1.0f - distance / maxDistance;
    }

    public static short[] convertToStereo(short[] audio, @Nullable Vector3d soundPos) {
        if (soundPos == null) {
            return PositionalAudioUtils.convertToStereo(audio);
        }
        return PositionalAudioUtils.convertToStereo(audio, PositionalAudioUtils.getStereoVolume(soundPos));
    }

    public static short[] convertToStereo(short[] audio, Vector3d cameraPos, float yRot, @Nullable Vector3d soundPos) {
        if (soundPos == null) {
            return PositionalAudioUtils.convertToStereo(audio);
        }
        return PositionalAudioUtils.convertToStereo(audio, PositionalAudioUtils.getStereoVolume(cameraPos, yRot, soundPos));
    }

    public static short[] convertToStereo(short[] audio) {
        short[] stereo = new short[audio.length * 2];
        for (int i = 0; i < audio.length; ++i) {
            stereo[i * 2] = audio[i];
            stereo[i * 2 + 1] = audio[i];
        }
        return stereo;
    }

    private static short[] convertToStereo(short[] audio, float volumeLeft, float volumeRight) {
        short[] stereo = new short[audio.length * 2];
        for (int i = 0; i < audio.length; ++i) {
            short left = (short)((float)audio[i] * volumeLeft);
            short right = (short)((float)audio[i] * volumeRight);
            stereo[i * 2] = left;
            stereo[i * 2 + 1] = right;
        }
        return stereo;
    }

    private static short[] convertToStereo(short[] audio, float[] volumes) {
        return PositionalAudioUtils.convertToStereo(audio, volumes[0], volumes[1]);
    }

    public static short[] convertToStereo(short[] audio, float volume) {
        return PositionalAudioUtils.convertToStereo(audio, volume, volume);
    }

    public static short[] convertToStereoForRecording(float maxDistance, Vector3d pos, short[] monoData) {
        return PositionalAudioUtils.convertToStereoForRecording(maxDistance, PositionalAudioUtils.mc.gameRenderer.getActiveRenderInfo().getProjectedView(), PositionalAudioUtils.mc.gameRenderer.getActiveRenderInfo().getYaw(), pos, monoData);
    }

    public static short[] convertToStereoForRecording(float maxDistance, Vector3d pos, short[] monoData, float volume) {
        return PositionalAudioUtils.convertToStereoForRecording(maxDistance, PositionalAudioUtils.mc.gameRenderer.getActiveRenderInfo().getProjectedView(), PositionalAudioUtils.mc.gameRenderer.getActiveRenderInfo().getYaw(), pos, monoData, volume);
    }

    public static short[] convertToStereoForRecording(float maxDistance, Vector3d cameraPos, float yRot, Vector3d pos, short[] monoData) {
        return PositionalAudioUtils.convertToStereoForRecording(maxDistance, cameraPos, yRot, pos, monoData, 1.0f);
    }

    public static short[] convertToStereoForRecording(float maxDistance, Vector3d cameraPos, float yRot, Vector3d pos, short[] monoData, float volume) {
        float distanceVolume = PositionalAudioUtils.getDistanceVolume(maxDistance, cameraPos, pos) * volume;
        if (!VoicechatClient.CLIENT_CONFIG.audioType.get().equals((Object)AudioType.OFF)) {
            float[] stereoVolume = PositionalAudioUtils.getStereoVolume(cameraPos, yRot, pos);
            return PositionalAudioUtils.convertToStereo(monoData, distanceVolume * stereoVolume[0], distanceVolume * stereoVolume[1]);
        }
        return PositionalAudioUtils.convertToStereo(monoData, distanceVolume, distanceVolume);
    }
}
