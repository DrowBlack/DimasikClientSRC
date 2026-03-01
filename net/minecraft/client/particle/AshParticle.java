package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class AshParticle
extends RisingParticle {
    protected AshParticle(ClientWorld world, double x, double y, double z, double motionMultX, double motionMultY, double motionMultZ, float scale, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z, 0.1f, -0.1f, 0.1f, motionMultX, motionMultY, motionMultZ, scale, spriteWithAge, 0.5f, 20, -0.004, false);
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new AshParticle(worldIn, x, y, z, 0.0, 0.0, 0.0, 1.0f, this.spriteSet);
        }
    }
}
