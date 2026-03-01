package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class IcebergFeature
extends Feature<BlockStateFeatureConfig> {
    public IcebergFeature(Codec<BlockStateFeatureConfig> p_i231964_1_) {
        super(p_i231964_1_);
    }

    @Override
    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlockStateFeatureConfig p_241855_5_) {
        boolean flag2;
        int l;
        p_241855_4_ = new BlockPos(p_241855_4_.getX(), p_241855_2_.func_230356_f_(), p_241855_4_.getZ());
        boolean flag = p_241855_3_.nextDouble() > 0.7;
        BlockState blockstate = p_241855_5_.state;
        double d0 = p_241855_3_.nextDouble() * 2.0 * Math.PI;
        int i = 11 - p_241855_3_.nextInt(5);
        int j = 3 + p_241855_3_.nextInt(3);
        boolean flag1 = p_241855_3_.nextDouble() > 0.7;
        int k = 11;
        int n = l = flag1 ? p_241855_3_.nextInt(6) + 6 : p_241855_3_.nextInt(15) + 3;
        if (!flag1 && p_241855_3_.nextDouble() > 0.9) {
            l += p_241855_3_.nextInt(19) + 7;
        }
        int i1 = Math.min(l + p_241855_3_.nextInt(11), 18);
        int j1 = Math.min(l + p_241855_3_.nextInt(7) - p_241855_3_.nextInt(5), 11);
        int k1 = flag1 ? i : 11;
        for (int l1 = -k1; l1 < k1; ++l1) {
            for (int i2 = -k1; i2 < k1; ++i2) {
                for (int j2 = 0; j2 < l; ++j2) {
                    int k2;
                    int n2 = k2 = flag1 ? this.func_205178_b(j2, l, j1) : this.func_205183_a(p_241855_3_, j2, l, j1);
                    if (!flag1 && l1 >= k2) continue;
                    this.func_205181_a(p_241855_1_, p_241855_3_, p_241855_4_, l, l1, j2, i2, k2, k1, flag1, j, d0, flag, blockstate);
                }
            }
        }
        this.func_205186_a(p_241855_1_, p_241855_4_, j1, l, flag1, i);
        for (int i3 = -k1; i3 < k1; ++i3) {
            for (int j3 = -k1; j3 < k1; ++j3) {
                for (int k3 = -1; k3 > -i1; --k3) {
                    int l3 = flag1 ? MathHelper.ceil((float)k1 * (1.0f - (float)Math.pow(k3, 2.0) / ((float)i1 * 8.0f))) : k1;
                    int l2 = this.func_205187_b(p_241855_3_, -k3, i1, j1);
                    if (i3 >= l2) continue;
                    this.func_205181_a(p_241855_1_, p_241855_3_, p_241855_4_, i1, i3, k3, j3, l2, l3, flag1, j, d0, flag, blockstate);
                }
            }
        }
        boolean bl = flag1 ? p_241855_3_.nextDouble() > 0.1 : (flag2 = p_241855_3_.nextDouble() > 0.7);
        if (flag2) {
            this.func_205184_a(p_241855_3_, p_241855_1_, j1, l, p_241855_4_, flag1, i, d0, j);
        }
        return true;
    }

    private void func_205184_a(Random rand, IWorld worldIn, int p_205184_3_, int p_205184_4_, BlockPos pos, boolean p_205184_6_, int p_205184_7_, double p_205184_8_, int p_205184_10_) {
        int i = rand.nextBoolean() ? -1 : 1;
        int j = rand.nextBoolean() ? -1 : 1;
        int k = rand.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
        if (rand.nextBoolean()) {
            k = p_205184_3_ / 2 + 1 - rand.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
        }
        int l = rand.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
        if (rand.nextBoolean()) {
            l = p_205184_3_ / 2 + 1 - rand.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
        }
        if (p_205184_6_) {
            k = l = rand.nextInt(Math.max(p_205184_7_ - 5, 1));
        }
        BlockPos blockpos = new BlockPos(i * k, 0, j * l);
        double d0 = p_205184_6_ ? p_205184_8_ + 1.5707963267948966 : rand.nextDouble() * 2.0 * Math.PI;
        for (int i1 = 0; i1 < p_205184_4_ - 3; ++i1) {
            int j1 = this.func_205183_a(rand, i1, p_205184_4_, p_205184_3_);
            this.func_205174_a(j1, i1, pos, worldIn, false, d0, blockpos, p_205184_7_, p_205184_10_);
        }
        for (int k1 = -1; k1 > -p_205184_4_ + rand.nextInt(5); --k1) {
            int l1 = this.func_205187_b(rand, -k1, p_205184_4_, p_205184_3_);
            this.func_205174_a(l1, k1, pos, worldIn, true, d0, blockpos, p_205184_7_, p_205184_10_);
        }
    }

    private void func_205174_a(int p_205174_1_, int yDiff, BlockPos p_205174_3_, IWorld worldIn, boolean placeWater, double p_205174_6_, BlockPos p_205174_8_, int p_205174_9_, int p_205174_10_) {
        int i = p_205174_1_ + 1 + p_205174_9_ / 3;
        int j = Math.min(p_205174_1_ - 3, 3) + p_205174_10_ / 2 - 1;
        for (int k = -i; k < i; ++k) {
            for (int l = -i; l < i; ++l) {
                BlockPos blockpos;
                Block block;
                double d0 = this.func_205180_a(k, l, p_205174_8_, i, j, p_205174_6_);
                if (!(d0 < 0.0) || !this.isIce(block = worldIn.getBlockState(blockpos = p_205174_3_.add(k, yDiff, l)).getBlock()) && block != Blocks.SNOW_BLOCK) continue;
                if (placeWater) {
                    this.setBlockState(worldIn, blockpos, Blocks.WATER.getDefaultState());
                    continue;
                }
                this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
                this.removeSnowLayer(worldIn, blockpos);
            }
        }
    }

    private void removeSnowLayer(IWorld worldIn, BlockPos posIn) {
        if (worldIn.getBlockState(posIn.up()).isIn(Blocks.SNOW)) {
            this.setBlockState(worldIn, posIn.up(), Blocks.AIR.getDefaultState());
        }
    }

    private void func_205181_a(IWorld worldIn, Random rand, BlockPos pos, int p_205181_4_, int xIn, int yIn, int zIn, int p_205181_8_, int p_205181_9_, boolean p_205181_10_, int p_205181_11_, double p_205181_12_, boolean p_205181_14_, BlockState p_205181_15_) {
        double d0;
        double d = d0 = p_205181_10_ ? this.func_205180_a(xIn, zIn, BlockPos.ZERO, p_205181_9_, this.func_205176_a(yIn, p_205181_4_, p_205181_11_), p_205181_12_) : this.func_205177_a(xIn, zIn, BlockPos.ZERO, p_205181_8_, rand);
        if (d0 < 0.0) {
            double d1;
            BlockPos blockpos = pos.add(xIn, yIn, zIn);
            double d2 = d1 = p_205181_10_ ? -0.5 : (double)(-6 - rand.nextInt(3));
            if (d0 > d1 && rand.nextDouble() > 0.9) {
                return;
            }
            this.func_205175_a(blockpos, worldIn, rand, p_205181_4_ - yIn, p_205181_4_, p_205181_10_, p_205181_14_, p_205181_15_);
        }
    }

    private void func_205175_a(BlockPos pos, IWorld worldIn, Random p_205175_3_, int p_205175_4_, int p_205175_5_, boolean p_205175_6_, boolean p_205175_7_, BlockState p_205175_8_) {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.getMaterial() == Material.AIR || blockstate.isIn(Blocks.SNOW_BLOCK) || blockstate.isIn(Blocks.ICE) || blockstate.isIn(Blocks.WATER)) {
            int i;
            boolean flag = !p_205175_6_ || p_205175_3_.nextDouble() > 0.05;
            int n = i = p_205175_6_ ? 3 : 2;
            if (p_205175_7_ && !blockstate.isIn(Blocks.WATER) && (double)p_205175_4_ <= (double)p_205175_3_.nextInt(Math.max(1, p_205175_5_ / i)) + (double)p_205175_5_ * 0.6 && flag) {
                this.setBlockState(worldIn, pos, Blocks.SNOW_BLOCK.getDefaultState());
            } else {
                this.setBlockState(worldIn, pos, p_205175_8_);
            }
        }
    }

    private int func_205176_a(int p_205176_1_, int p_205176_2_, int p_205176_3_) {
        int i = p_205176_3_;
        if (p_205176_1_ > 0 && p_205176_2_ - p_205176_1_ <= 3) {
            i = p_205176_3_ - (4 - (p_205176_2_ - p_205176_1_));
        }
        return i;
    }

    private double func_205177_a(int p_205177_1_, int p_205177_2_, BlockPos pos, int p_205177_4_, Random rand) {
        float f = 10.0f * MathHelper.clamp(rand.nextFloat(), 0.2f, 0.8f) / (float)p_205177_4_;
        return (double)f + Math.pow(p_205177_1_ - pos.getX(), 2.0) + Math.pow(p_205177_2_ - pos.getZ(), 2.0) - Math.pow(p_205177_4_, 2.0);
    }

    private double func_205180_a(int xIn, int zIn, BlockPos pos, int p_205180_4_, int p_205180_5_, double p_205180_6_) {
        return Math.pow(((double)(xIn - pos.getX()) * Math.cos(p_205180_6_) - (double)(zIn - pos.getZ()) * Math.sin(p_205180_6_)) / (double)p_205180_4_, 2.0) + Math.pow(((double)(xIn - pos.getX()) * Math.sin(p_205180_6_) + (double)(zIn - pos.getZ()) * Math.cos(p_205180_6_)) / (double)p_205180_5_, 2.0) - 1.0;
    }

    private int func_205183_a(Random rand, int p_205183_2_, int p_205183_3_, int p_205183_4_) {
        float f = 3.5f - rand.nextFloat();
        float f1 = (1.0f - (float)Math.pow(p_205183_2_, 2.0) / ((float)p_205183_3_ * f)) * (float)p_205183_4_;
        if (p_205183_3_ > 15 + rand.nextInt(5)) {
            int i = p_205183_2_ < 3 + rand.nextInt(6) ? p_205183_2_ / 2 : p_205183_2_;
            f1 = (1.0f - (float)i / ((float)p_205183_3_ * f * 0.4f)) * (float)p_205183_4_;
        }
        return MathHelper.ceil(f1 / 2.0f);
    }

    private int func_205178_b(int p_205178_1_, int p_205178_2_, int p_205178_3_) {
        float f = 1.0f;
        float f1 = (1.0f - (float)Math.pow(p_205178_1_, 2.0) / ((float)p_205178_2_ * 1.0f)) * (float)p_205178_3_;
        return MathHelper.ceil(f1 / 2.0f);
    }

    private int func_205187_b(Random rand, int p_205187_2_, int p_205187_3_, int p_205187_4_) {
        float f = 1.0f + rand.nextFloat() / 2.0f;
        float f1 = (1.0f - (float)p_205187_2_ / ((float)p_205187_3_ * f)) * (float)p_205187_4_;
        return MathHelper.ceil(f1 / 2.0f);
    }

    private boolean isIce(Block blockIn) {
        return blockIn == Blocks.PACKED_ICE || blockIn == Blocks.SNOW_BLOCK || blockIn == Blocks.BLUE_ICE;
    }

    private boolean isAirBellow(IBlockReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial() == Material.AIR;
    }

    private void func_205186_a(IWorld worldIn, BlockPos pos, int p_205186_3_, int p_205186_4_, boolean p_205186_5_, int p_205186_6_) {
        int i = p_205186_5_ ? p_205186_6_ : p_205186_3_ / 2;
        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                for (int l = 0; l <= p_205186_4_; ++l) {
                    BlockPos blockpos = pos.add(j, l, k);
                    Block block = worldIn.getBlockState(blockpos).getBlock();
                    if (!this.isIce(block) && block != Blocks.SNOW) continue;
                    if (this.isAirBellow(worldIn, blockpos)) {
                        this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
                        this.setBlockState(worldIn, blockpos.up(), Blocks.AIR.getDefaultState());
                        continue;
                    }
                    if (!this.isIce(block)) continue;
                    Block[] ablock = new Block[]{worldIn.getBlockState(blockpos.west()).getBlock(), worldIn.getBlockState(blockpos.east()).getBlock(), worldIn.getBlockState(blockpos.north()).getBlock(), worldIn.getBlockState(blockpos.south()).getBlock()};
                    int i1 = 0;
                    for (Block block1 : ablock) {
                        if (this.isIce(block1)) continue;
                        ++i1;
                    }
                    if (i1 < 3) continue;
                    this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }
}
