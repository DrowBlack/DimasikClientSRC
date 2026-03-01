package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.main.render.EventGameOverlay;
import dimasik.events.main.visual.EventFog;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;

public class FogRenderer {
    public static float red;
    public static float green;
    public static float blue;
    private static int lastWaterFogColor;
    private static int waterFogColor;
    private static long waterFogUpdateTime;
    public static boolean fogStandard;

    public static void updateFogColor(ActiveRenderInfo activeRenderInfoIn, float partialTicks, ClientWorld worldIn, int renderDistanceChunks, float bossColorModifier) {
        Entity entity1;
        Vector3d vector3d2;
        FluidState fluidstate = activeRenderInfoIn.getFluidState();
        if (fluidstate.isTagged(FluidTags.WATER)) {
            long i = Util.milliTime();
            int j = worldIn.getBiome(new BlockPos(activeRenderInfoIn.getProjectedView())).getWaterFogColor();
            if (waterFogUpdateTime < 0L) {
                lastWaterFogColor = j;
                waterFogColor = j;
                waterFogUpdateTime = i;
            }
            int k = lastWaterFogColor >> 16 & 0xFF;
            int l = lastWaterFogColor >> 8 & 0xFF;
            int i1 = lastWaterFogColor & 0xFF;
            int j1 = waterFogColor >> 16 & 0xFF;
            int k1 = waterFogColor >> 8 & 0xFF;
            int l1 = waterFogColor & 0xFF;
            float f = MathHelper.clamp((float)(i - waterFogUpdateTime) / 5000.0f, 0.0f, 1.0f);
            float f1 = MathHelper.lerp(f, j1, k);
            float f2 = MathHelper.lerp(f, k1, l);
            float f3 = MathHelper.lerp(f, l1, i1);
            red = f1 / 255.0f;
            green = f2 / 255.0f;
            blue = f3 / 255.0f;
            if (lastWaterFogColor != j) {
                lastWaterFogColor = j;
                waterFogColor = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
                waterFogUpdateTime = i;
            }
        } else if (fluidstate.isTagged(FluidTags.LAVA)) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
            waterFogUpdateTime = -1L;
        } else {
            float f16;
            float f4 = 0.25f + 0.75f * (float)renderDistanceChunks / 32.0f;
            f4 = 1.0f - (float)Math.pow(f4, 0.25);
            Vector3d vector3d = worldIn.getSkyColor(activeRenderInfoIn.getBlockPos(), partialTicks);
            vector3d = CustomColors.getWorldSkyColor(vector3d, worldIn, activeRenderInfoIn.getRenderViewEntity(), partialTicks);
            float f5 = (float)vector3d.x;
            float f8 = (float)vector3d.y;
            float f11 = (float)vector3d.z;
            float f12 = MathHelper.clamp(MathHelper.cos(worldIn.func_242415_f(partialTicks) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeManager biomemanager = worldIn.getBiomeManager();
            Vector3d vector3d3 = activeRenderInfoIn.getProjectedView().subtract(2.0, 2.0, 2.0).scale(0.25);
            Vector3d vector3d4 = CubicSampler.func_240807_a_(vector3d3, (p_lambda$updateFogColor$0_3_, p_lambda$updateFogColor$0_4_, p_lambda$updateFogColor$0_5_) -> worldIn.func_239132_a_().func_230494_a_(Vector3d.unpack(biomemanager.getBiomeAtPosition(p_lambda$updateFogColor$0_3_, p_lambda$updateFogColor$0_4_, p_lambda$updateFogColor$0_5_).getFogColor()), f12));
            vector3d4 = CustomColors.getWorldFogColor(vector3d4, worldIn, activeRenderInfoIn.getRenderViewEntity(), partialTicks);
            EventFog eventFog = new EventFog(CustomColors.getWorldFogColor(vector3d4, worldIn, activeRenderInfoIn.getRenderViewEntity(), partialTicks));
            Load.getInstance().getEvents().call(eventFog);
            red = (float)eventFog.getColor().getX();
            green = (float)eventFog.getColor().getY();
            blue = (float)eventFog.getColor().getZ();
            if (renderDistanceChunks >= 4) {
                float[] afloat;
                float f13 = MathHelper.sin(worldIn.getCelestialAngleRadians(partialTicks)) > 0.0f ? -1.0f : 1.0f;
                Vector3f vector3f = new Vector3f(f13, 0.0f, 0.0f);
                float f17 = activeRenderInfoIn.getViewVector().dot(vector3f);
                if (f17 < 0.0f) {
                    f17 = 0.0f;
                }
                if (f17 > 0.0f && (afloat = worldIn.func_239132_a_().func_230492_a_(worldIn.func_242415_f(partialTicks), partialTicks)) != null) {
                    red = red * (1.0f - (f17 *= afloat[3])) + afloat[0] * f17;
                    green = green * (1.0f - f17) + afloat[1] * f17;
                    blue = blue * (1.0f - f17) + afloat[2] * f17;
                }
            }
            red += (f5 - red) * f4;
            green += (f8 - green) * f4;
            blue += (f11 - blue) * f4;
            float f14 = worldIn.getRainStrength(partialTicks);
            if (f14 > 0.0f) {
                float f15 = 1.0f - f14 * 0.5f;
                float f18 = 1.0f - f14 * 0.4f;
                red *= f15;
                green *= f15;
                blue *= f18;
            }
            if ((f16 = worldIn.getThunderStrength(partialTicks)) > 0.0f) {
                float f19 = 1.0f - f16 * 0.5f;
                red *= f19;
                green *= f19;
                blue *= f19;
            }
            waterFogUpdateTime = -1L;
        }
        double d0 = activeRenderInfoIn.getProjectedView().y * worldIn.getWorldInfo().getFogDistance();
        if (activeRenderInfoIn.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).isPotionActive(Effects.BLINDNESS)) {
            int i2 = ((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).getActivePotionEffect(Effects.BLINDNESS).getDuration();
            d0 = i2 < 20 ? (d0 *= (double)(1.0f - (float)i2 / 20.0f)) : 0.0;
        }
        if (d0 < 1.0 && !fluidstate.isTagged(FluidTags.LAVA)) {
            if (d0 < 0.0) {
                d0 = 0.0;
            }
            d0 *= d0;
            red = (float)((double)red * d0);
            green = (float)((double)green * d0);
            blue = (float)((double)blue * d0);
        }
        if (bossColorModifier > 0.0f) {
            red = red * (1.0f - bossColorModifier) + red * 0.7f * bossColorModifier;
            green = green * (1.0f - bossColorModifier) + green * 0.6f * bossColorModifier;
            blue = blue * (1.0f - bossColorModifier) + blue * 0.6f * bossColorModifier;
        }
        if (fluidstate.isTagged(FluidTags.WATER)) {
            float f9;
            float f6 = 0.0f;
            if (activeRenderInfoIn.getRenderViewEntity() instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)activeRenderInfoIn.getRenderViewEntity();
                f6 = clientplayerentity.getWaterBrightness();
            }
            if (Float.isInfinite(f9 = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue)))) {
                f9 = Math.nextAfter(f9, 0.0);
            }
            red = red * (1.0f - f6) + red * f9 * f6;
            green = green * (1.0f - f6) + green * f9 * f6;
            blue = blue * (1.0f - f6) + blue * f9 * f6;
        } else if (activeRenderInfoIn.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).isPotionActive(Effects.NIGHT_VISION)) {
            float f7 = GameRenderer.getNightVisionBrightness((LivingEntity)activeRenderInfoIn.getRenderViewEntity(), partialTicks);
            float f10 = Math.min(1.0f / red, Math.min(1.0f / green, 1.0f / blue));
            if (Float.isInfinite(f10)) {
                f10 = Math.nextAfter(f10, 0.0);
            }
            red = red * (1.0f - f7) + red * f10 * f7;
            green = green * (1.0f - f7) + green * f10 * f7;
            blue = blue * (1.0f - f7) + blue * f10 * f7;
        }
        if (fluidstate.isTagged(FluidTags.WATER)) {
            Entity entity = activeRenderInfoIn.getRenderViewEntity();
            Vector3d vector3d1 = CustomColors.getUnderwaterColor(worldIn, entity.getPosX(), entity.getPosY() + 1.0, entity.getPosZ());
            if (vector3d1 != null) {
                red = (float)vector3d1.x;
                green = (float)vector3d1.y;
                blue = (float)vector3d1.z;
            }
        } else if (fluidstate.isTagged(FluidTags.LAVA) && (vector3d2 = CustomColors.getUnderlavaColor(worldIn, (entity1 = activeRenderInfoIn.getRenderViewEntity()).getPosX(), entity1.getPosY() + 1.0, entity1.getPosZ())) != null) {
            red = (float)vector3d2.x;
            green = (float)vector3d2.y;
            blue = (float)vector3d2.z;
        }
        if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
            Object object = Reflector.newInstance(Reflector.EntityViewRenderEvent_FogColors_Constructor, activeRenderInfoIn, Float.valueOf(partialTicks), Float.valueOf(red), Float.valueOf(green), Float.valueOf(blue));
            Reflector.postForgeBusEvent(object);
            red = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_FogColors_getRed, new Object[0]);
            green = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_FogColors_getGreen, new Object[0]);
            blue = Reflector.callFloat(object, Reflector.EntityViewRenderEvent_FogColors_getBlue, new Object[0]);
        }
        Shaders.setClearColor(red, green, blue, 0.0f);
        RenderSystem.clearColor(red, green, blue, 0.0f);
    }

    public static void resetFog() {
        RenderSystem.fogDensity(0.0f);
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
    }

    public static void setupFog(ActiveRenderInfo activeRenderInfoIn, FogType fogTypeIn, float farPlaneDistance, boolean nearFog) {
        FogRenderer.setupFog(activeRenderInfoIn, fogTypeIn, farPlaneDistance, nearFog, 0.0f);
    }

    public static void setupFog(ActiveRenderInfo p_setupFog_0_, FogType p_setupFog_1_, float p_setupFog_2_, boolean p_setupFog_3_, float p_setupFog_4_) {
        fogStandard = false;
        FluidState fluidstate = p_setupFog_0_.getFluidState();
        Entity entity = p_setupFog_0_.getRenderViewEntity();
        float f = -1.0f;
        if (Reflector.ForgeHooksClient_getFogDensity.exists()) {
            f = Reflector.callFloat(Reflector.ForgeHooksClient_getFogDensity, new Object[]{p_setupFog_1_, p_setupFog_0_, Float.valueOf(p_setupFog_4_), Float.valueOf(0.1f)});
        }
        if (f >= 0.0f) {
            GlStateManager.fogDensity(f);
        } else if (fluidstate.isTagged(FluidTags.WATER)) {
            EventGameOverlay eventGameOverlay = new EventGameOverlay(EventGameOverlay.OverlayType.WaterFog);
            Load.getInstance().getEvents().call(eventGameOverlay);
            if (!eventGameOverlay.isCancelled()) {
                float f1 = 1.0f;
                f1 = 0.05f;
                if (entity instanceof ClientPlayerEntity) {
                    ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)entity;
                    f1 -= clientplayerentity.getWaterBrightness() * clientplayerentity.getWaterBrightness() * 0.03f;
                    Biome biome = clientplayerentity.world.getBiome(clientplayerentity.getPosition());
                    if (biome.getCategory() == Biome.Category.SWAMP) {
                        f1 += 0.005f;
                    }
                }
                RenderSystem.fogDensity(f1);
                RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
            }
        } else {
            float f4;
            float f3;
            EventGameOverlay eventGameOverlay = new EventGameOverlay(EventGameOverlay.OverlayType.LavaFog);
            Load.getInstance().getEvents().call(eventGameOverlay);
            EventGameOverlay eventGameOverlayBlind = new EventGameOverlay(EventGameOverlay.OverlayType.Blindness);
            Load.getInstance().getEvents().call(eventGameOverlayBlind);
            if (fluidstate.isTagged(FluidTags.LAVA) && !eventGameOverlay.isCancelled()) {
                if (entity instanceof LivingEntity && ((LivingEntity)entity).isPotionActive(Effects.FIRE_RESISTANCE)) {
                    f3 = 0.0f;
                    f4 = 3.0f;
                } else {
                    f3 = 0.25f;
                    f4 = 1.0f;
                }
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).isPotionActive(Effects.BLINDNESS) && !eventGameOverlayBlind.isCancelled()) {
                int i = ((LivingEntity)entity).getActivePotionEffect(Effects.BLINDNESS).getDuration();
                float f2 = MathHelper.lerp(Math.min(1.0f, (float)i / 20.0f), p_setupFog_2_, 5.0f);
                if (p_setupFog_1_ == FogType.FOG_SKY) {
                    f3 = 0.0f;
                    f4 = f2 * 0.8f;
                } else {
                    f3 = f2 * 0.25f;
                    f4 = f2;
                }
            } else if (p_setupFog_3_) {
                fogStandard = true;
                f3 = p_setupFog_2_ * 0.05f;
                f4 = Math.min(p_setupFog_2_, 192.0f) * 0.5f;
            } else if (p_setupFog_1_ == FogType.FOG_SKY) {
                fogStandard = true;
                f3 = 0.0f;
                f4 = p_setupFog_2_;
            } else {
                fogStandard = true;
                f3 = p_setupFog_2_;
                EventFog eventFog = new EventFog(Config.getFogStart());
                Load.getInstance().getEvents().call(eventFog);
                f3 *= eventFog.getDistance();
                f4 = p_setupFog_2_;
            }
            RenderSystem.fogStart(f3);
            RenderSystem.fogEnd(f4);
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            RenderSystem.setupNvFogDistance();
        }
    }

    public static void applyFog() {
        RenderSystem.fog(2918, red, green, blue, 1.0f);
        if (Config.isShaders()) {
            Shaders.setFogColor(red, green, blue);
        }
    }

    static {
        lastWaterFogColor = -1;
        waterFogColor = -1;
        waterFogUpdateTime = -1L;
        fogStandard = false;
    }

    public static enum FogType {
        FOG_SKY,
        FOG_TERRAIN;

    }
}
