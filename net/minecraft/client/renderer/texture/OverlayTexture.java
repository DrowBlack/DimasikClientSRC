package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.optifine.shaders.Shaders;

public class OverlayTexture
implements AutoCloseable {
    public static final int NO_OVERLAY = OverlayTexture.getPackedUV(0, 10);
    private final DynamicTexture texture = new DynamicTexture(16, 16, false);

    public OverlayTexture() {
        NativeImage nativeimage = this.texture.getTextureData();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i < 8) {
                    nativeimage.setPixelRGBA(j, i, -1308622593);
                    continue;
                }
                int k = (int)((1.0f - (float)j / 15.0f * 0.75f) * 255.0f);
                nativeimage.setPixelRGBA(j, i, k << 24 | 0xFFFFFF);
            }
        }
        RenderSystem.activeTexture(33985);
        this.texture.bindTexture();
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = 0.06666667f;
        RenderSystem.scalef(0.06666667f, 0.06666667f, 0.06666667f);
        RenderSystem.matrixMode(5888);
        this.texture.bindTexture();
        nativeimage.uploadTextureSub(0, 0, 0, 0, 0, nativeimage.getWidth(), nativeimage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(33984);
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void setupOverlayColor() {
        if (!Shaders.isOverlayDisabled()) {
            RenderSystem.setupOverlayColor(this.texture::getGlTextureId, 16);
        }
    }

    public static int getU(float uIn) {
        return (int)(uIn * 15.0f);
    }

    public static int getV(boolean hurtIn) {
        return hurtIn ? 3 : 10;
    }

    public static int getPackedUV(int uIn, int vIn) {
        return uIn | vIn << 16;
    }

    public static int getPackedUV(float uIn, boolean hurtIn) {
        return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(hurtIn));
    }

    public void teardownOverlayColor() {
        if (!Shaders.isOverlayDisabled()) {
            RenderSystem.teardownOverlayColor();
        }
    }
}
