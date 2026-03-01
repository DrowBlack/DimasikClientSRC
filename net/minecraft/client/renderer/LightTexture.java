package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.shaders.Shaders;

public class LightTexture
implements AutoCloseable {
    private final DynamicTexture dynamicTexture;
    private final NativeImage nativeImage;
    private final ResourceLocation resourceLocation;
    private boolean needsUpdate;
    private float torchFlicker;
    private final GameRenderer entityRenderer;
    private final Minecraft client;
    private boolean allowed = true;
    private boolean custom = false;
    private Vector3f tempVector = new Vector3f();
    public static final int MAX_BRIGHTNESS = LightTexture.packLight(15, 15);

    public LightTexture(GameRenderer entityRendererIn, Minecraft mcIn) {
        this.entityRenderer = entityRendererIn;
        this.client = mcIn;
        this.dynamicTexture = new DynamicTexture(16, 16, false);
        this.resourceLocation = this.client.getTextureManager().getDynamicTextureLocation("light_map", this.dynamicTexture);
        this.nativeImage = this.dynamicTexture.getTextureData();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                this.nativeImage.setPixelRGBA(j, i, -1);
            }
        }
        this.dynamicTexture.updateDynamicTexture();
    }

    @Override
    public void close() {
        this.dynamicTexture.close();
    }

    public void tick() {
        this.torchFlicker = (float)((double)this.torchFlicker + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.torchFlicker = (float)((double)this.torchFlicker * 0.9);
        this.needsUpdate = true;
    }

    public void disableLightmap() {
        RenderSystem.activeTexture(33986);
        RenderSystem.disableTexture();
        RenderSystem.activeTexture(33984);
        if (Config.isShaders()) {
            Shaders.disableLightmap();
        }
    }

    public void enableLightmap() {
        if (this.allowed) {
            RenderSystem.activeTexture(33986);
            RenderSystem.matrixMode(5890);
            RenderSystem.loadIdentity();
            float f = 0.00390625f;
            RenderSystem.scalef(0.00390625f, 0.00390625f, 0.00390625f);
            RenderSystem.translatef(8.0f, 8.0f, 8.0f);
            RenderSystem.matrixMode(5888);
            this.client.getTextureManager().bindTexture(this.resourceLocation);
            RenderSystem.texParameter(3553, 10241, 9729);
            RenderSystem.texParameter(3553, 10240, 9729);
            RenderSystem.texParameter(3553, 10242, 33071);
            RenderSystem.texParameter(3553, 10243, 33071);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableTexture();
            RenderSystem.activeTexture(33984);
            if (Config.isShaders()) {
                Shaders.enableLightmap();
            }
        }
    }

    public void updateLightmap(float partialTicks) {
        if (this.needsUpdate) {
            this.needsUpdate = false;
            this.client.getProfiler().startSection("lightTex");
            ClientWorld clientworld = this.client.world;
            if (clientworld != null) {
                this.custom = false;
                if (Config.isCustomColors()) {
                    boolean flag;
                    boolean bl = flag = this.client.player.isPotionActive(Effects.NIGHT_VISION) || this.client.player.isPotionActive(Effects.CONDUIT_POWER);
                    if (CustomColors.updateLightmap(clientworld, this.torchFlicker, this.nativeImage, flag, partialTicks)) {
                        this.dynamicTexture.updateDynamicTexture();
                        this.needsUpdate = false;
                        this.client.getProfiler().endSection();
                        this.custom = true;
                        return;
                    }
                }
                float f9 = clientworld.getSunBrightness(1.0f);
                float f = clientworld.getTimeLightningFlash() > 0 ? 1.0f : f9 * 0.95f + 0.05f;
                float f1 = this.client.player.getWaterBrightness();
                float f2 = this.client.player.isPotionActive(Effects.NIGHT_VISION) ? GameRenderer.getNightVisionBrightness(this.client.player, partialTicks) : (f1 > 0.0f && this.client.player.isPotionActive(Effects.CONDUIT_POWER) ? f1 : 0.0f);
                Vector3f vector3f = new Vector3f(f9, f9, 1.0f);
                vector3f.lerp(new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
                float f3 = this.torchFlicker + 1.5f;
                Vector3f vector3f1 = new Vector3f();
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        float f10;
                        float f4 = this.getLightBrightness(clientworld, i) * f;
                        float f5 = this.getLightBrightness(clientworld, j) * f3;
                        float f6 = f5 * ((f5 * 0.6f + 0.4f) * 0.6f + 0.4f);
                        float f7 = f5 * (f5 * f5 * 0.6f + 0.4f);
                        vector3f1.set(f5, f6, f7);
                        if (clientworld.func_239132_a_().func_241684_d_()) {
                            vector3f1.lerp(this.getTempVector3f(0.99f, 1.12f, 1.0f), 0.25f);
                        } else {
                            Vector3f vector3f2 = this.getTempCopy(vector3f);
                            vector3f2.mul(f4);
                            vector3f1.add(vector3f2);
                            vector3f1.lerp(this.getTempVector3f(0.75f, 0.75f, 0.75f), 0.04f);
                            if (this.entityRenderer.getBossColorModifier(partialTicks) > 0.0f) {
                                float f8 = this.entityRenderer.getBossColorModifier(partialTicks);
                                Vector3f vector3f3 = this.getTempCopy(vector3f1);
                                vector3f3.mul(0.7f, 0.6f, 0.6f);
                                vector3f1.lerp(vector3f3, f8);
                            }
                        }
                        vector3f1.clamp(0.0f, 1.0f);
                        if (f2 > 0.0f && (f10 = Math.max(vector3f1.getX(), Math.max(vector3f1.getY(), vector3f1.getZ()))) < 1.0f) {
                            float f12 = 1.0f / f10;
                            Vector3f vector3f5 = this.getTempCopy(vector3f1);
                            vector3f5.mul(f12);
                            vector3f1.lerp(vector3f5, f2);
                        }
                        float f11 = (float)this.client.gameSettings.gamma;
                        Vector3f vector3f4 = this.getTempCopy(vector3f1);
                        vector3f4.apply(this::invGamma);
                        vector3f1.lerp(vector3f4, f11);
                        vector3f1.lerp(this.getTempVector3f(0.75f, 0.75f, 0.75f), 0.04f);
                        vector3f1.clamp(0.0f, 1.0f);
                        vector3f1.mul(255.0f);
                        int j1 = 255;
                        int k = (int)vector3f1.getX();
                        int l = (int)vector3f1.getY();
                        int i1 = (int)vector3f1.getZ();
                        this.nativeImage.setPixelRGBA(j, i, 0xFF000000 | i1 << 16 | l << 8 | k);
                    }
                }
                this.dynamicTexture.updateDynamicTexture();
                this.client.getProfiler().endSection();
            }
        }
    }

    private float invGamma(float valueIn) {
        float f = 1.0f - valueIn;
        return 1.0f - f * f * f * f;
    }

    private float getLightBrightness(World worldIn, int lightLevelIn) {
        return worldIn.getDimensionType().getAmbientLight(lightLevelIn);
    }

    public static int packLight(int blockLightIn, int skyLightIn) {
        return blockLightIn << 4 | skyLightIn << 20;
    }

    public static int getLightBlock(int packedLightIn) {
        return (packedLightIn & 0xFFFF) >> 4;
    }

    public static int getLightSky(int packedLightIn) {
        return packedLightIn >> 20 & 0xFFFF;
    }

    private Vector3f getTempVector3f(float p_getTempVector3f_1_, float p_getTempVector3f_2_, float p_getTempVector3f_3_) {
        this.tempVector.set(p_getTempVector3f_1_, p_getTempVector3f_2_, p_getTempVector3f_3_);
        return this.tempVector;
    }

    private Vector3f getTempCopy(Vector3f p_getTempCopy_1_) {
        this.tempVector.set(p_getTempCopy_1_.getX(), p_getTempCopy_1_.getY(), p_getTempCopy_1_.getZ());
        return this.tempVector;
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    public void setAllowed(boolean p_setAllowed_1_) {
        this.allowed = p_setAllowed_1_;
    }

    public boolean isCustom() {
        return this.custom;
    }
}
