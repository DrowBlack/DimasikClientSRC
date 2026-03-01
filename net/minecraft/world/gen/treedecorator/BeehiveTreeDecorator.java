package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class BeehiveTreeDecorator
extends TreeDecorator {
    public static final Codec<BeehiveTreeDecorator> field_236863_a_ = ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).xmap(BeehiveTreeDecorator::new, p_236865_0_ -> Float.valueOf(p_236865_0_.probability)).codec();
    private final float probability;

    public BeehiveTreeDecorator(float probabilityIn) {
        this.probability = probabilityIn;
    }

    @Override
    protected TreeDecoratorType<?> func_230380_a_() {
        return TreeDecoratorType.BEEHIVE;
    }

    @Override
    public void func_225576_a_(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
        if (!(p_225576_2_.nextFloat() >= this.probability)) {
            BlockPos blockpos;
            BlockPos blockpos1;
            Direction direction = BeehiveBlock.getGenerationDirection(p_225576_2_);
            int i = !p_225576_4_.isEmpty() ? Math.max(p_225576_4_.get(0).getY() - 1, p_225576_3_.get(0).getY()) : Math.min(p_225576_3_.get(0).getY() + 1 + p_225576_2_.nextInt(3), p_225576_3_.get(p_225576_3_.size() - 1).getY());
            List list = p_225576_3_.stream().filter(p_236864_1_ -> p_236864_1_.getY() == i).collect(Collectors.toList());
            if (!list.isEmpty() && Feature.isAirAt(p_225576_1_, blockpos1 = (blockpos = (BlockPos)list.get(p_225576_2_.nextInt(list.size()))).offset(direction)) && Feature.isAirAt(p_225576_1_, blockpos1.offset(Direction.SOUTH))) {
                BlockState blockstate = (BlockState)Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH);
                this.func_227423_a_(p_225576_1_, blockpos1, blockstate, p_225576_5_, p_225576_6_);
                TileEntity tileentity = p_225576_1_.getTileEntity(blockpos1);
                if (tileentity instanceof BeehiveTileEntity) {
                    BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
                    int j = 2 + p_225576_2_.nextInt(2);
                    for (int k = 0; k < j; ++k) {
                        BeeEntity beeentity = new BeeEntity((EntityType<? extends BeeEntity>)EntityType.BEE, (World)p_225576_1_.getWorld());
                        beehivetileentity.tryEnterHive(beeentity, false, p_225576_2_.nextInt(599));
                    }
                }
            }
        }
    }
}
