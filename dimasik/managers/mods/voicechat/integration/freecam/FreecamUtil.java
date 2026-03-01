package dimasik.managers.mods.voicechat.integration.freecam;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.integration.freecam.FreecamMode;
import dimasik.managers.mods.voicechat.voice.client.PositionalAudioUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;

public class FreecamUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    public static boolean isFreecamEnabled() {
        if (FreecamUtil.mc.player == null) {
            return false;
        }
        return VoicechatClient.CLIENT_CONFIG.freecamMode.get().equals((Object)FreecamMode.PLAYER) && !FreecamUtil.mc.player.isSpectator() && !FreecamUtil.mc.player.equals(mc.getRenderViewEntity());
    }

    public static Vector3d getReferencePoint() {
        if (FreecamUtil.mc.player == null) {
            return Vector3d.ZERO;
        }
        return FreecamUtil.isFreecamEnabled() ? FreecamUtil.mc.player.getEyePosition(1.0f) : FreecamUtil.mc.gameRenderer.getActiveRenderInfo().getProjectedView();
    }

    public static double getDistanceTo(Vector3d pos) {
        return FreecamUtil.getReferencePoint().distanceTo(pos);
    }

    public static float getDistanceVolume(float maxDistance, Vector3d pos) {
        return PositionalAudioUtils.getDistanceVolume(maxDistance, FreecamUtil.getReferencePoint(), pos);
    }
}
