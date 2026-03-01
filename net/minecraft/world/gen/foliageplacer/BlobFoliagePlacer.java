package net.minecraft.world.gen.foliageplacer;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;

public class BlobFoliagePlacer
extends FoliagePlacer {
    public static final Codec<BlobFoliagePlacer> field_236738_a_ = RecordCodecBuilder.create(p_236742_0_ -> BlobFoliagePlacer.func_236740_a_(p_236742_0_).apply((Applicative)p_236742_0_, BlobFoliagePlacer::new));
    protected final int field_236739_b_;

    protected static <P extends BlobFoliagePlacer> Products.P3<RecordCodecBuilder.Mu<P>, FeatureSpread, FeatureSpread, Integer> func_236740_a_(RecordCodecBuilder.Instance<P> p_236740_0_) {
        return BlobFoliagePlacer.func_242830_b(p_236740_0_).and(((MapCodec)Codec.intRange(0, 16).fieldOf("height")).forGetter(p_236741_0_ -> p_236741_0_.field_236739_b_));
    }

    public BlobFoliagePlacer(FeatureSpread p_i241995_1_, FeatureSpread p_i241995_2_, int p_i241995_3_) {
        super(p_i241995_1_, p_i241995_2_);
        this.field_236739_b_ = p_i241995_3_;
    }

    @Override
    protected FoliagePlacerType<?> func_230371_a_() {
        return FoliagePlacerType.BLOB;
    }

    @Override
    protected void func_230372_a_(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_) {
        for (int i = p_230372_9_; i >= p_230372_9_ - p_230372_6_; --i) {
            int j = Math.max(p_230372_7_ + p_230372_5_.func_236764_b_() - 1 - i / 2, 0);
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, p_230372_5_.func_236763_a_(), j, p_230372_8_, i, p_230372_5_.func_236765_c_(), p_230372_10_);
        }
    }

    @Override
    public int func_230374_a_(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_) {
        return this.field_236739_b_;
    }

    @Override
    protected boolean func_230373_a_(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_) {
        return p_230373_2_ == p_230373_5_ && p_230373_4_ == p_230373_5_ && (p_230373_1_.nextInt(2) == 0 || p_230373_3_ == 0);
    }
}
