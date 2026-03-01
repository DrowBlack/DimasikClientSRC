package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ItemParticleData;

public class BreakingParticle
extends SpriteTexturedParticle {
    private final float u;
    private final float v;

    private BreakingParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, ItemStack stack) {
        this(world, x, y, z, stack);
        this.motionX *= (double)0.1f;
        this.motionY *= (double)0.1f;
        this.motionZ *= (double)0.1f;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.TERRAIN_SHEET;
    }

    protected BreakingParticle(ClientWorld world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, world, null).getParticleTexture());
        this.particleGravity = 1.0f;
        this.particleScale /= 2.0f;
        this.u = this.rand.nextFloat() * 3.0f;
        this.v = this.rand.nextFloat() * 3.0f;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getInterpolatedU((this.u + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getInterpolatedU(this.u / 4.0f * 16.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getInterpolatedV(this.v / 4.0f * 16.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getInterpolatedV((this.v + 1.0f) / 4.0f * 16.0f);
    }

    public static class SnowballFactory
    implements IParticleFactory<BasicParticleType> {
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BreakingParticle(worldIn, x, y, z, new ItemStack(Items.SNOWBALL));
        }
    }

    public static class SlimeFactory
    implements IParticleFactory<BasicParticleType> {
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BreakingParticle(worldIn, x, y, z, new ItemStack(Items.SLIME_BALL));
        }
    }

    public static class Factory
    implements IParticleFactory<ItemParticleData> {
        @Override
        public Particle makeParticle(ItemParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new BreakingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getItemStack());
        }
    }
}
