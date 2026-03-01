package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class CoralClawFeature
extends CoralFeature {
    public CoralClawFeature(Codec<NoFeatureConfig> p_i231939_1_) {
        super(p_i231939_1_);
    }

    @Override
    protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_) {
        if (!this.func_204624_b(p_204623_1_, p_204623_2_, p_204623_3_, p_204623_4_)) {
            return false;
        }
        Direction direction = Direction.Plane.HORIZONTAL.random(p_204623_2_);
        int i = p_204623_2_.nextInt(2) + 2;
        ArrayList<Direction> list = Lists.newArrayList(direction, direction.rotateY(), direction.rotateYCCW());
        Collections.shuffle(list, p_204623_2_);
        block0: for (Direction direction1 : list.subList(0, i)) {
            int k;
            Direction direction2;
            BlockPos.Mutable blockpos$mutable = p_204623_3_.toMutable();
            int j = p_204623_2_.nextInt(2) + 1;
            blockpos$mutable.move(direction1);
            if (direction1 == direction) {
                direction2 = direction;
                k = p_204623_2_.nextInt(3) + 2;
            } else {
                blockpos$mutable.move(Direction.UP);
                Direction[] adirection = new Direction[]{direction1, Direction.UP};
                direction2 = Util.getRandomObject(adirection, p_204623_2_);
                k = p_204623_2_.nextInt(3) + 3;
            }
            for (int l = 0; l < j && this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutable, p_204623_4_); ++l) {
                blockpos$mutable.move(direction2);
            }
            blockpos$mutable.move(direction2.getOpposite());
            blockpos$mutable.move(Direction.UP);
            for (int i1 = 0; i1 < k; ++i1) {
                blockpos$mutable.move(direction);
                if (!this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutable, p_204623_4_)) continue block0;
                if (!(p_204623_2_.nextFloat() < 0.25f)) continue;
                blockpos$mutable.move(Direction.UP);
            }
        }
        return true;
    }
}
