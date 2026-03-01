package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FallingDustParticle
extends SpriteTexturedParticle {
    private final float rotSpeed;
    private final IAnimatedSprite spriteWithAge;

    private FallingDustParticle(ClientWorld world, double x, double y, double z, float red, float green, float blue, IAnimatedSprite spriteWithAge) {
        super(world, x, y, z);
        this.spriteWithAge = spriteWithAge;
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        float f = 0.9f;
        this.particleScale *= 0.67499995f;
        int i = (int)(32.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)i * 0.9f, 1.0f);
        this.selectSpriteWithAge(spriteWithAge);
        this.rotSpeed = ((float)Math.random() - 0.5f) * 0.1f;
        this.particleAngle = (float)Math.random() * ((float)Math.PI * 2);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getScale(float scaleFactor) {
        return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
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
            this.prevParticleAngle = this.particleAngle;
            this.particleAngle += (float)Math.PI * this.rotSpeed * 2.0f;
            if (this.onGround) {
                this.particleAngle = 0.0f;
                this.prevParticleAngle = 0.0f;
            }
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionY -= (double)0.003f;
            this.motionY = Math.max(this.motionY, (double)-0.14f);
        }
    }

    public static class Factory
    implements IParticleFactory<BlockParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSetIn) {
            this.spriteSet = spriteSetIn;
        }

        @Override
        @Nullable
        public Particle makeParticle(BlockParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BlockState blockstate = typeIn.getBlockState();
            if (!blockstate.isAir() && blockstate.getRenderType() == BlockRenderType.INVISIBLE) {
                return null;
            }
            BlockPos blockpos = new BlockPos(x, y, z);
            int i = Minecraft.getInstance().getBlockColors().getColorOrMaterialColor(blockstate, worldIn, blockpos);
            if (blockstate.getBlock() instanceof FallingBlock) {
                i = ((FallingBlock)blockstate.getBlock()).getDustColor(blockstate, worldIn, blockpos);
            }
            float f = (float)(i >> 16 & 0xFF) / 255.0f;
            float f1 = (float)(i >> 8 & 0xFF) / 255.0f;
            float f2 = (float)(i & 0xFF) / 255.0f;
            return new FallingDustParticle(worldIn, x, y, z, f, f1, f2, this.spriteSet);
        }
    }
}
