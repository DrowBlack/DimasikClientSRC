package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

public class DragonBreathParticle
extends SpriteTexturedParticle {
    private boolean hasHitGround;
    private final IAnimatedSprite spriteWithAge;

    private DragonBreathParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.particleRed = MathHelper.nextFloat(this.rand, 0.7176471f, 0.8745098f);
        this.particleGreen = MathHelper.nextFloat(this.rand, 0.0f, 0.0f);
        this.particleBlue = MathHelper.nextFloat(this.rand, 0.8235294f, 0.9764706f);
        this.particleScale *= 0.75f;
        this.maxAge = (int)(20.0 / ((double)this.rand.nextFloat() * 0.8 + 0.2));
        this.hasHitGround = false;
        this.canCollide = false;
        this.spriteWithAge = spriteWithAge;
        this.selectSpriteWithAge(spriteWithAge);
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
            if (this.onGround) {
                this.motionY = 0.0;
                this.hasHitGround = true;
            }
            if (this.hasHitGround) {
                this.motionY += 0.002;
            }
            this.move(this.motionX, this.motionY, this.motionZ);
            if (this.posY == this.prevPosY) {
                this.motionX *= 1.1;
                this.motionZ *= 1.1;
            }
            this.motionX *= (double)0.96f;
            this.motionZ *= (double)0.96f;
            if (this.hasHitGround) {
                this.motionY *= (double)0.96f;
            }
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getScale(float scaleFactor) {
        return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DragonBreathParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
