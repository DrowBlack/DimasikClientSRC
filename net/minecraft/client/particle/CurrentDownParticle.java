package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CurrentDownParticle
extends SpriteTexturedParticle {
    private float motionAngle;

    private CurrentDownParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        this.maxAge = (int)(Math.random() * 60.0) + 30;
        this.canCollide = false;
        this.motionX = 0.0;
        this.motionY = -0.05;
        this.motionZ = 0.0;
        this.setSize(0.02f, 0.02f);
        this.particleScale *= this.rand.nextFloat() * 0.6f + 0.2f;
        this.particleGravity = 0.002f;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            float f = 0.6f;
            this.motionX += (double)(0.6f * MathHelper.cos(this.motionAngle));
            this.motionZ += (double)(0.6f * MathHelper.sin(this.motionAngle));
            this.motionX *= 0.07;
            this.motionZ *= 0.07;
            this.move(this.motionX, this.motionY, this.motionZ);
            if (!this.world.getFluidState(new BlockPos(this.posX, this.posY, this.posZ)).isTagged(FluidTags.WATER) || this.onGround) {
                this.setExpired();
            }
            this.motionAngle = (float)((double)this.motionAngle + 0.08);
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
            CurrentDownParticle currentdownparticle = new CurrentDownParticle(worldIn, x, y, z);
            currentdownparticle.selectSpriteRandomly(this.spriteSet);
            return currentdownparticle;
        }
    }
}
