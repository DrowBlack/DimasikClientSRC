package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    public static final Codec<TrunkVineTreeDecorator> field_236878_a_;
    public static final TrunkVineTreeDecorator field_236879_b_;

    @Override
    protected TreeDecoratorType<?> func_230380_a_() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void func_225576_a_(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
        p_225576_3_.forEach(p_236880_5_ -> {
            BlockPos blockpos3;
            BlockPos blockpos2;
            BlockPos blockpos1;
            BlockPos blockpos;
            if (p_225576_2_.nextInt(3) > 0 && Feature.isAirAt(p_225576_1_, blockpos = p_236880_5_.west())) {
                this.func_227424_a_(p_225576_1_, blockpos, VineBlock.EAST, p_225576_5_, p_225576_6_);
            }
            if (p_225576_2_.nextInt(3) > 0 && Feature.isAirAt(p_225576_1_, blockpos1 = p_236880_5_.east())) {
                this.func_227424_a_(p_225576_1_, blockpos1, VineBlock.WEST, p_225576_5_, p_225576_6_);
            }
            if (p_225576_2_.nextInt(3) > 0 && Feature.isAirAt(p_225576_1_, blockpos2 = p_236880_5_.north())) {
                this.func_227424_a_(p_225576_1_, blockpos2, VineBlock.SOUTH, p_225576_5_, p_225576_6_);
            }
            if (p_225576_2_.nextInt(3) > 0 && Feature.isAirAt(p_225576_1_, blockpos3 = p_236880_5_.south())) {
                this.func_227424_a_(p_225576_1_, blockpos3, VineBlock.NORTH, p_225576_5_, p_225576_6_);
            }
        });
    }

    static {
        field_236879_b_ = new TrunkVineTreeDecorator();
        field_236878_a_ = Codec.unit(() -> field_236879_b_);
    }
}
