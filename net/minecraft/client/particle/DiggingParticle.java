package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;

public class DiggingParticle
extends SpriteTexturedParticle {
    private final BlockState sourceState;
    private BlockPos sourcePos;
    private final float u;
    private final float v;

    public DiggingParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, BlockState state) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.sourceState = state;
        this.setSprite(Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
        this.particleGravity = 1.0f;
        this.particleRed = 0.6f;
        this.particleGreen = 0.6f;
        this.particleBlue = 0.6f;
        this.particleScale /= 2.0f;
        this.u = this.rand.nextFloat() * 3.0f;
        this.v = this.rand.nextFloat() * 3.0f;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.TERRAIN_SHEET;
    }

    public DiggingParticle setBlockPos(BlockPos pos) {
        this.sourcePos = pos;
        if (this.sourceState.isIn(Blocks.GRASS_BLOCK)) {
            return this;
        }
        this.multiplyColor(pos);
        return this;
    }

    public DiggingParticle init() {
        this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
        if (this.sourceState.isIn(Blocks.GRASS_BLOCK)) {
            return this;
        }
        this.multiplyColor(this.sourcePos);
        return this;
    }

    protected void multiplyColor(@Nullable BlockPos pos) {
        int i = Minecraft.getInstance().getBlockColors().getColor(this.sourceState, this.world, pos, 0);
        this.particleRed *= (float)(i >> 16 & 0xFF) / 255.0f;
        this.particleGreen *= (float)(i >> 8 & 0xFF) / 255.0f;
        this.particleBlue *= (float)(i & 0xFF) / 255.0f;
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

    @Override
    public int getBrightnessForRender(float partialTick) {
        int i = super.getBrightnessForRender(partialTick);
        int j = 0;
        if (this.world.isBlockLoaded(this.sourcePos)) {
            j = WorldRenderer.getCombinedLight(this.world, this.sourcePos);
        }
        return i == 0 ? j : i;
    }

    public static class Factory
    implements IParticleFactory<BlockParticleData> {
        @Override
        public Particle makeParticle(BlockParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            BlockState blockstate = typeIn.getBlockState();
            return !blockstate.isAir() && !blockstate.isIn(Blocks.MOVING_PISTON) ? new DiggingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, blockstate).init() : null;
        }
    }
}
