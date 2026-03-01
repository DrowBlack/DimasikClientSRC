package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.MobAppearanceParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.BlockPosM;

public abstract class Particle {
    private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    protected final ClientWorld world;
    protected double prevPosX;
    protected double prevPosY;
    protected double prevPosZ;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double motionX;
    protected double motionY;
    protected double motionZ;
    private AxisAlignedBB boundingBox = EMPTY_AABB;
    protected boolean onGround;
    protected boolean canCollide = true;
    private boolean collidedY;
    protected boolean isExpired;
    protected float width = 0.6f;
    protected float height = 1.8f;
    protected final Random rand = new Random();
    protected int age;
    protected int maxAge;
    protected float particleGravity;
    protected float particleRed = 1.0f;
    protected float particleGreen = 1.0f;
    protected float particleBlue = 1.0f;
    protected float particleAlpha = 1.0f;
    protected float particleAngle;
    protected float prevParticleAngle;
    private BlockPosM blockPosM = new BlockPosM();

    protected Particle(ClientWorld world, double x, double y, double z) {
        this.world = world;
        this.setSize(0.2f, 0.2f);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.maxAge = (int)(4.0f / (this.rand.nextFloat() * 0.9f + 0.1f));
    }

    public Particle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        this(world, x, y, z);
        this.motionX = motionX + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.motionY = motionY + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.motionZ = motionZ + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        float f = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double)f1 * (double)f * (double)0.4f;
        this.motionY = this.motionY / (double)f1 * (double)f * (double)0.4f + (double)0.1f;
        this.motionZ = this.motionZ / (double)f1 * (double)f * (double)0.4f;
    }

    public Particle multiplyVelocity(float multiplier) {
        this.motionX *= (double)multiplier;
        this.motionY = (this.motionY - (double)0.1f) * (double)multiplier + (double)0.1f;
        this.motionZ *= (double)multiplier;
        return this;
    }

    public Particle multiplyParticleScaleBy(float scale) {
        this.setSize(0.2f * scale, 0.2f * scale);
        return this;
    }

    public void setColor(float particleRedIn, float particleGreenIn, float particleBlueIn) {
        this.particleRed = particleRedIn;
        this.particleGreen = particleGreenIn;
        this.particleBlue = particleBlueIn;
    }

    protected void setAlphaF(float alpha) {
        this.particleAlpha = alpha;
    }

    public void setMaxAge(int particleLifeTime) {
        this.maxAge = particleLifeTime;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.motionY -= 0.04 * (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.98f;
            this.motionY *= (double)0.98f;
            this.motionZ *= (double)0.98f;
            if (this.onGround) {
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
            }
        }
    }

    public abstract void renderParticle(IVertexBuilder var1, ActiveRenderInfo var2, float var3);

    public abstract IParticleRenderType getRenderType();

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.age;
    }

    public void setExpired() {
        this.isExpired = true;
    }

    protected void setSize(float particleWidth, float particleHeight) {
        if (particleWidth != this.width || particleHeight != this.height) {
            this.width = particleWidth;
            this.height = particleHeight;
            AxisAlignedBB axisalignedbb = this.getBoundingBox();
            double d0 = (axisalignedbb.minX + axisalignedbb.maxX - (double)particleWidth) / 2.0;
            double d1 = (axisalignedbb.minZ + axisalignedbb.maxZ - (double)particleWidth) / 2.0;
            this.setBoundingBox(new AxisAlignedBB(d0, axisalignedbb.minY, d1, d0 + (double)this.width, axisalignedbb.minY + (double)this.height, d1 + (double)this.width));
        }
    }

    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0f;
        float f1 = this.height;
        this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }

    public void move(double x, double y, double z) {
        if (!this.collidedY) {
            double d0 = x;
            double d1 = y;
            double d2 = z;
            if (this.canCollide && (x != 0.0 || y != 0.0 || z != 0.0) && this.hasNearBlocks(x, y, z)) {
                Vector3d vector3d = Entity.collideBoundingBoxHeuristically(null, new Vector3d(x, y, z), this.getBoundingBox(), this.world, ISelectionContext.dummy(), new ReuseableStream<VoxelShape>(Stream.empty()));
                x = vector3d.x;
                y = vector3d.y;
                z = vector3d.z;
            }
            if (x != 0.0 || y != 0.0 || z != 0.0) {
                this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
                this.resetPositionToBB();
            }
            if (Math.abs(d1) >= (double)1.0E-5f && Math.abs(y) < (double)1.0E-5f) {
                this.collidedY = true;
            }
            boolean bl = this.onGround = d1 != y && d1 < 0.0;
            if (d0 != x) {
                this.motionX = 0.0;
            }
            if (d2 != z) {
                this.motionZ = 0.0;
            }
        }
    }

    protected void resetPositionToBB() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0;
        this.posY = axisalignedbb.minY;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0;
    }

    protected int getBrightnessForRender(float partialTick) {
        BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
        return this.world.isBlockLoaded(blockpos) ? WorldRenderer.getCombinedLight(this.world, blockpos) : 0;
    }

    public boolean isAlive() {
        return !this.isExpired;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB bb) {
        this.boundingBox = bb;
    }

    private boolean hasNearBlocks(double p_hasNearBlocks_1_, double p_hasNearBlocks_3_, double p_hasNearBlocks_5_) {
        if (!(this.width > 1.0f) && !(this.height > 1.0f)) {
            double d1;
            double d0;
            int i = MathHelper.floor(this.posX);
            int j = MathHelper.floor(this.posY);
            int k = MathHelper.floor(this.posZ);
            this.blockPosM.setXyz(i, j, k);
            BlockState blockstate = this.world.getBlockState(this.blockPosM);
            if (!blockstate.isAir()) {
                return true;
            }
            double d = p_hasNearBlocks_1_ > 0.0 ? this.boundingBox.maxX : (d0 = p_hasNearBlocks_1_ < 0.0 ? this.boundingBox.minX : this.posX);
            double d2 = p_hasNearBlocks_3_ > 0.0 ? this.boundingBox.maxY : (d1 = p_hasNearBlocks_3_ < 0.0 ? this.boundingBox.minY : this.posY);
            double d22 = p_hasNearBlocks_5_ > 0.0 ? this.boundingBox.maxZ : (p_hasNearBlocks_5_ < 0.0 ? this.boundingBox.minZ : this.posZ);
            int l = MathHelper.floor(d0 + p_hasNearBlocks_1_);
            int i1 = MathHelper.floor(d1 + p_hasNearBlocks_3_);
            int j1 = MathHelper.floor(d22 + p_hasNearBlocks_5_);
            if (l != i || i1 != j || j1 != k) {
                this.blockPosM.setXyz(l, i1, j1);
                BlockState blockstate1 = this.world.getBlockState(this.blockPosM);
                if (!blockstate1.isAir()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean shouldCull() {
        return !(this instanceof MobAppearanceParticle);
    }
}
