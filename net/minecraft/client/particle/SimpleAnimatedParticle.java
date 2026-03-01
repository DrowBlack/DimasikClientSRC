package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;

public class SimpleAnimatedParticle
extends SpriteTexturedParticle {
    protected final IAnimatedSprite spriteWithAge;
    private final float yAccel;
    private float baseAirFriction = 0.91f;
    private float fadeTargetRed;
    private float fadeTargetGreen;
    private float fadeTargetBlue;
    private boolean fadingColor;

    protected SimpleAnimatedParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite spriteWithAge, float yAccel) {
        super(world, x, y, z);
        this.spriteWithAge = spriteWithAge;
        this.yAccel = yAccel;
    }

    public void setColor(int color) {
        float f = (float)((color & 0xFF0000) >> 16) / 255.0f;
        float f1 = (float)((color & 0xFF00) >> 8) / 255.0f;
        float f2 = (float)((color & 0xFF) >> 0) / 255.0f;
        float f3 = 1.0f;
        this.setColor(f * 1.0f, f1 * 1.0f, f2 * 1.0f);
    }

    public void setColorFade(int rgb) {
        this.fadeTargetRed = (float)((rgb & 0xFF0000) >> 16) / 255.0f;
        this.fadeTargetGreen = (float)((rgb & 0xFF00) >> 8) / 255.0f;
        this.fadeTargetBlue = (float)((rgb & 0xFF) >> 0) / 255.0f;
        this.fadingColor = true;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteWithAge);
            if (this.age > this.maxAge / 2) {
                this.setAlphaF(1.0f - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
                if (this.fadingColor) {
                    this.particleRed += (this.fadeTargetRed - this.particleRed) * 0.2f;
                    this.particleGreen += (this.fadeTargetGreen - this.particleGreen) * 0.2f;
                    this.particleBlue += (this.fadeTargetBlue - this.particleBlue) * 0.2f;
                }
            }
            this.motionY += (double)this.yAccel;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)this.baseAirFriction;
            this.motionY *= (double)this.baseAirFriction;
            this.motionZ *= (double)this.baseAirFriction;
            if (this.onGround) {
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
            }
        }
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
    }

    protected void setBaseAirFriction(float airFriction) {
        this.baseAirFriction = airFriction;
    }
}
