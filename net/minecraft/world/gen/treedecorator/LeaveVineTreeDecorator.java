package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class LeaveVineTreeDecorator
extends TreeDecorator {
    public static final Codec<LeaveVineTreeDecorator> field_236870_a_;
    public static final LeaveVineTreeDecorator field_236871_b_;

    @Override
    protected TreeDecoratorType<?> func_230380_a_() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    @Override
    public void func_225576_a_(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
        p_225576_4_.forEach(p_242866_5_ -> {
            BlockPos blockpos3;
            BlockPos blockpos2;
            BlockPos blockpos1;
            BlockPos blockpos;
            if (p_225576_2_.nextInt(4) == 0 && Feature.isAirAt(p_225576_1_, blockpos = p_242866_5_.west())) {
                this.func_227420_a_(p_225576_1_, blockpos, VineBlock.EAST, p_225576_5_, p_225576_6_);
            }
            if (p_225576_2_.nextInt(4) == 0 && Feature.isAirAt(p_225576_1_, blockpos1 = p_242866_5_.east())) {
                this.func_227420_a_(p_225576_1_, blockpos1, VineBlock.WEST, p_225576_5_, p_225576_6_);
            }
            if (p_225576_2_.nextInt(4) == 0 && Feature.isAirAt(p_225576_1_, blockpos2 = p_242866_5_.north())) {
                this.func_227420_a_(p_225576_1_, blockpos2, VineBlock.SOUTH, p_225576_5_, p_225576_6_);
            }
            if (p_225576_2_.nextInt(4) == 0 && Feature.isAirAt(p_225576_1_, blockpos3 = p_242866_5_.south())) {
                this.func_227420_a_(p_225576_1_, blockpos3, VineBlock.NORTH, p_225576_5_, p_225576_6_);
            }
        });
    }

    private void func_227420_a_(IWorldGenerationReader p_227420_1_, BlockPos p_227420_2_, BooleanProperty p_227420_3_, Set<BlockPos> p_227420_4_, MutableBoundingBox p_227420_5_) {
        this.func_227424_a_(p_227420_1_, p_227420_2_, p_227420_3_, p_227420_4_, p_227420_5_);
        BlockPos blockpos = p_227420_2_.down();
        for (int i = 4; Feature.isAirAt(p_227420_1_, blockpos) && i > 0; --i) {
            this.func_227424_a_(p_227420_1_, blockpos, p_227420_3_, p_227420_4_, p_227420_5_);
            blockpos = blockpos.down();
        }
    }

    static {
        field_236871_b_ = new LeaveVineTreeDecorator();
        field_236870_a_ = Codec.unit(() -> field_236871_b_);
    }
}
