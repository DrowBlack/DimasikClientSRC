package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class FancyTrunkPlacer
extends AbstractTrunkPlacer {
    public static final Codec<FancyTrunkPlacer> field_236884_a_ = RecordCodecBuilder.create(p_236891_0_ -> FancyTrunkPlacer.func_236915_a_(p_236891_0_).apply(p_236891_0_, FancyTrunkPlacer::new));

    public FancyTrunkPlacer(int p_i232054_1_, int p_i232054_2_, int p_i232054_3_) {
        super(p_i232054_1_, p_i232054_2_, p_i232054_3_);
    }

    @Override
    protected TrunkPlacerType<?> func_230381_a_() {
        return TrunkPlacerType.FANCY_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_) {
        int j1;
        int i = 5;
        int j = p_230382_3_ + 2;
        int k = MathHelper.floor((double)j * 0.618);
        if (!p_230382_7_.forcePlacement) {
            FancyTrunkPlacer.func_236909_a_(p_230382_1_, p_230382_4_.down());
        }
        double d0 = 1.0;
        int l = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)j / 13.0, 2.0)));
        int i1 = p_230382_4_.getY() + k;
        ArrayList<Foliage> list = Lists.newArrayList();
        list.add(new Foliage(p_230382_4_.up(j1), i1));
        for (j1 = j - 5; j1 >= 0; --j1) {
            float f = this.func_236890_b_(j, j1);
            if (f < 0.0f) continue;
            for (int k1 = 0; k1 < l; ++k1) {
                BlockPos blockpos1;
                double d5;
                double d3;
                double d1 = 1.0;
                double d2 = 1.0 * (double)f * ((double)p_230382_2_.nextFloat() + 0.328);
                double d4 = d2 * Math.sin(d3 = (double)(p_230382_2_.nextFloat() * 2.0f) * Math.PI) + 0.5;
                BlockPos blockpos = p_230382_4_.add(d4, (double)(j1 - 1), d5 = d2 * Math.cos(d3) + 0.5);
                if (!this.func_236887_a_(p_230382_1_, p_230382_2_, blockpos, blockpos1 = blockpos.up(5), false, p_230382_5_, p_230382_6_, p_230382_7_)) continue;
                int l1 = p_230382_4_.getX() - blockpos.getX();
                int i2 = p_230382_4_.getZ() - blockpos.getZ();
                double d6 = (double)blockpos.getY() - Math.sqrt(l1 * l1 + i2 * i2) * 0.381;
                int j2 = d6 > (double)i1 ? i1 : (int)d6;
                BlockPos blockpos2 = new BlockPos(p_230382_4_.getX(), j2, p_230382_4_.getZ());
                if (!this.func_236887_a_(p_230382_1_, p_230382_2_, blockpos2, blockpos, false, p_230382_5_, p_230382_6_, p_230382_7_)) continue;
                list.add(new Foliage(blockpos, blockpos2.getY()));
            }
        }
        this.func_236887_a_(p_230382_1_, p_230382_2_, p_230382_4_, p_230382_4_.up(k), true, p_230382_5_, p_230382_6_, p_230382_7_);
        this.func_236886_a_(p_230382_1_, p_230382_2_, j, p_230382_4_, list, p_230382_5_, p_230382_6_, p_230382_7_);
        ArrayList<FoliagePlacer.Foliage> list1 = Lists.newArrayList();
        for (Foliage fancytrunkplacer$foliage : list) {
            if (!this.func_236885_a_(j, fancytrunkplacer$foliage.func_236894_a_() - p_230382_4_.getY())) continue;
            list1.add(fancytrunkplacer$foliage.field_236892_a_);
        }
        return list1;
    }

    private boolean func_236887_a_(IWorldGenerationReader p_236887_1_, Random p_236887_2_, BlockPos p_236887_3_, BlockPos p_236887_4_, boolean p_236887_5_, Set<BlockPos> p_236887_6_, MutableBoundingBox p_236887_7_, BaseTreeFeatureConfig p_236887_8_) {
        if (!p_236887_5_ && Objects.equals(p_236887_3_, p_236887_4_)) {
            return true;
        }
        BlockPos blockpos = p_236887_4_.add(-p_236887_3_.getX(), -p_236887_3_.getY(), -p_236887_3_.getZ());
        int i = this.func_236888_a_(blockpos);
        float f = (float)blockpos.getX() / (float)i;
        float f1 = (float)blockpos.getY() / (float)i;
        float f2 = (float)blockpos.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos blockpos1 = p_236887_3_.add(0.5f + (float)j * f, 0.5f + (float)j * f1, 0.5f + (float)j * f2);
            if (p_236887_5_) {
                FancyTrunkPlacer.func_236913_a_(p_236887_1_, blockpos1, (BlockState)p_236887_8_.trunkProvider.getBlockState(p_236887_2_, blockpos1).with(RotatedPillarBlock.AXIS, this.func_236889_a_(p_236887_3_, blockpos1)), p_236887_7_);
                p_236887_6_.add(blockpos1.toImmutable());
                continue;
            }
            if (TreeFeature.func_236410_c_(p_236887_1_, blockpos1)) continue;
            return false;
        }
        return true;
    }

    private int func_236888_a_(BlockPos p_236888_1_) {
        int i = MathHelper.abs(p_236888_1_.getX());
        int j = MathHelper.abs(p_236888_1_.getY());
        int k = MathHelper.abs(p_236888_1_.getZ());
        return Math.max(i, Math.max(j, k));
    }

    private Direction.Axis func_236889_a_(BlockPos p_236889_1_, BlockPos p_236889_2_) {
        int j;
        Direction.Axis direction$axis = Direction.Axis.Y;
        int i = Math.abs(p_236889_2_.getX() - p_236889_1_.getX());
        int k = Math.max(i, j = Math.abs(p_236889_2_.getZ() - p_236889_1_.getZ()));
        if (k > 0) {
            direction$axis = i == k ? Direction.Axis.X : Direction.Axis.Z;
        }
        return direction$axis;
    }

    private boolean func_236885_a_(int p_236885_1_, int p_236885_2_) {
        return (double)p_236885_2_ >= (double)p_236885_1_ * 0.2;
    }

    private void func_236886_a_(IWorldGenerationReader p_236886_1_, Random p_236886_2_, int p_236886_3_, BlockPos p_236886_4_, List<Foliage> p_236886_5_, Set<BlockPos> p_236886_6_, MutableBoundingBox p_236886_7_, BaseTreeFeatureConfig p_236886_8_) {
        for (Foliage fancytrunkplacer$foliage : p_236886_5_) {
            int i = fancytrunkplacer$foliage.func_236894_a_();
            BlockPos blockpos = new BlockPos(p_236886_4_.getX(), i, p_236886_4_.getZ());
            if (blockpos.equals(fancytrunkplacer$foliage.field_236892_a_.func_236763_a_()) || !this.func_236885_a_(p_236886_3_, i - p_236886_4_.getY())) continue;
            this.func_236887_a_(p_236886_1_, p_236886_2_, blockpos, fancytrunkplacer$foliage.field_236892_a_.func_236763_a_(), true, p_236886_6_, p_236886_7_, p_236886_8_);
        }
    }

    private float func_236890_b_(int p_236890_1_, int p_236890_2_) {
        if ((float)p_236890_2_ < (float)p_236890_1_ * 0.3f) {
            return -1.0f;
        }
        float f = (float)p_236890_1_ / 2.0f;
        float f1 = f - (float)p_236890_2_;
        float f2 = MathHelper.sqrt(f * f - f1 * f1);
        if (f1 == 0.0f) {
            f2 = f;
        } else if (Math.abs(f1) >= f) {
            return 0.0f;
        }
        return f2 * 0.5f;
    }

    static class Foliage {
        private final FoliagePlacer.Foliage field_236892_a_;
        private final int field_236893_b_;

        public Foliage(BlockPos p_i232055_1_, int p_i232055_2_) {
            this.field_236892_a_ = new FoliagePlacer.Foliage(p_i232055_1_, 0, false);
            this.field_236893_b_ = p_i232055_2_;
        }

        public int func_236894_a_() {
            return this.field_236893_b_;
        }
    }
}
