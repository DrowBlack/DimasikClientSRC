package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class CocoaTreeDecorator
extends TreeDecorator {
    public static final Codec<CocoaTreeDecorator> field_236866_a_ = ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).xmap(CocoaTreeDecorator::new, p_236868_0_ -> Float.valueOf(p_236868_0_.field_227417_b_)).codec();
    private final float field_227417_b_;

    public CocoaTreeDecorator(float p_i225868_1_) {
        this.field_227417_b_ = p_i225868_1_;
    }

    @Override
    protected TreeDecoratorType<?> func_230380_a_() {
        return TreeDecoratorType.COCOA;
    }

    @Override
    public void func_225576_a_(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
        if (!(p_225576_2_.nextFloat() >= this.field_227417_b_)) {
            int i = p_225576_3_.get(0).getY();
            p_225576_3_.stream().filter(p_236867_1_ -> p_236867_1_.getY() - i <= 2).forEach(p_242865_5_ -> {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    Direction direction1;
                    BlockPos blockpos;
                    if (!(p_225576_2_.nextFloat() <= 0.25f) || !Feature.isAirAt(p_225576_1_, blockpos = p_242865_5_.add((direction1 = direction.getOpposite()).getXOffset(), 0, direction1.getZOffset()))) continue;
                    BlockState blockstate = (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, p_225576_2_.nextInt(3))).with(CocoaBlock.HORIZONTAL_FACING, direction);
                    this.func_227423_a_(p_225576_1_, blockpos, blockstate, p_225576_5_, p_225576_6_);
                }
            });
        }
    }
}
