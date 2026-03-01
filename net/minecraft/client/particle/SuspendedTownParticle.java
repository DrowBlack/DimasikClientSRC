package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class SuspendedTownParticle
extends SpriteTexturedParticle {
    private SuspendedTownParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        float f;
        this.particleRed = f = this.rand.nextFloat() * 0.1f + 0.2f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.setSize(0.02f, 0.02f);
        this.particleScale *= this.rand.nextFloat() * 0.6f + 0.5f;
        this.motionX *= (double)0.02f;
        this.motionY *= (double)0.02f;
        this.motionZ *= (double)0.02f;
        this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.maxAge-- <= 0) {
            this.setExpired();
        } else {
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.99;
            this.motionY *= 0.99;
            this.motionZ *= 0.99;
        }
    }

    public static class HappyVillagerFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public HappyVillagerFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            suspendedtownparticle.selectSpriteRandomly(this.spriteSet);
            suspendedtownparticle.setColor(1.0f, 1.0f, 1.0f);
            return suspendedtownparticle;
        }
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            suspendedtownparticle.selectSpriteRandomly(this.spriteSet);
            return suspendedtownparticle;
        }
    }

    public static class DolphinSpeedFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public DolphinSpeedFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            suspendedtownparticle.setColor(0.3f, 0.5f, 1.0f);
            suspendedtownparticle.selectSpriteRandomly(this.spriteSet);
            suspendedtownparticle.setAlphaF(1.0f - worldIn.rand.nextFloat() * 0.7f);
            suspendedtownparticle.setMaxAge(suspendedtownparticle.getMaxAge() / 2);
            return suspendedtownparticle;
        }
    }

    public static class ComposterFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public ComposterFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            suspendedtownparticle.selectSpriteRandomly(this.spriteSet);
            suspendedtownparticle.setColor(1.0f, 1.0f, 1.0f);
            suspendedtownparticle.setMaxAge(3 + worldIn.getRandom().nextInt(5));
            return suspendedtownparticle;
        }
    }
}
