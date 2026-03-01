package dimasik.utils.client;

import com.mojang.blaze3d.platform.GlStateManager;
import dimasik.helpers.interfaces.IFastAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class AnimationMath
implements IFastAccess {
    public static double deltaTime() {
        return Minecraft.debugFPS > 0 ? 1.0 / (double)Minecraft.debugFPS : 1.0;
    }

    public static float fast(float end, float start, float multiple) {
        return (1.0f - MathHelper.clamp((float)(AnimationMath.deltaTime() * (double)multiple), 0.0f, 1.0f)) * end + MathHelper.clamp((float)(AnimationMath.deltaTime() * (double)multiple), 0.0f, 1.0f) * start;
    }

    public static float lerp(float end, float start, float multiple) {
        return (float)((double)end + (double)(start - end) * MathHelper.clamp(AnimationMath.deltaTime() * (double)multiple, 0.0, 1.0));
    }

    public static double lerp(double end, double start, double multiple) {
        return end + (start - end) * MathHelper.clamp(AnimationMath.deltaTime() * multiple, 0.0, 1.0);
    }

    public static void sizeAnimation(double width, double height, double scale) {
        GlStateManager.translated(width, height, 0.0);
        GlStateManager.scaled(scale, scale, scale);
        GlStateManager.translated(-width, -height, 0.0);
    }
}
