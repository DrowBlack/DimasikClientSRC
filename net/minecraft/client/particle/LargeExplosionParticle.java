package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class LargeExplosionParticle
extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteWithAge;

    private LargeExplosionParticle(ClientWorld world, double x, double y, double z, double scale, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        float f;
        this.maxAge = 6 + this.rand.nextInt(4);
        this.particleRed = f = this.rand.nextFloat() * 0.6f + 0.4f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.particleScale = 2.0f * (1.0f - (float)scale * 0.5f);
        this.spriteWithAge = spriteWithAge;
        this.selectSpriteWithAge(spriteWithAge);
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
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
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LargeExplosionParticle(worldIn, x, y, z, xSpeed, this.spriteSet);
        }
    }
}
