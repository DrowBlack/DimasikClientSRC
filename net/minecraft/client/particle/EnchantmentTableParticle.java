package net.minecraft.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class EnchantmentTableParticle
extends SpriteTexturedParticle {
    private final double coordX;
    private final double coordY;
    private final double coordZ;

    private EnchantmentTableParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.coordX = x;
        this.coordY = y;
        this.coordZ = z;
        this.prevPosX = x + motionX;
        this.prevPosY = y + motionY;
        this.prevPosZ = z + motionZ;
        this.posX = this.prevPosX;
        this.posY = this.prevPosY;
        this.posZ = this.prevPosZ;
        this.particleScale = 0.1f * (this.rand.nextFloat() * 0.5f + 0.2f);
        float f = this.rand.nextFloat() * 0.6f + 0.4f;
        this.particleRed = 0.9f * f;
        this.particleGreen = 0.9f * f;
        this.particleBlue = f;
        this.canCollide = false;
        this.maxAge = (int)(Math.random() * 10.0) + 30;
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
    public int getBrightnessForRender(float partialTick) {
        int i = super.getBrightnessForRender(partialTick);
        float f = (float)this.age / (float)this.maxAge;
        f *= f;
        f *= f;
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((k += (int)(f * 15.0f * 16.0f)) > 240) {
            k = 240;
        }
        return j | k << 16;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            float f = (float)this.age / (float)this.maxAge;
            f = 1.0f - f;
            float f1 = 1.0f - f;
            f1 *= f1;
            f1 *= f1;
            this.posX = this.coordX + this.motionX * (double)f;
            this.posY = this.coordY + this.motionY * (double)f - (double)(f1 * 1.2f);
            this.posZ = this.coordZ + this.motionZ * (double)f;
        }
    }

    public static class NautilusFactory
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public NautilusFactory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            enchantmenttableparticle.selectSpriteRandomly(this.spriteSet);
            return enchantmenttableparticle;
        }
    }

    public static class EnchantmentTable
    implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public EnchantmentTable(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            enchantmenttableparticle.selectSpriteRandomly(this.spriteSet);
            return enchantmenttableparticle;
        }
    }
}
