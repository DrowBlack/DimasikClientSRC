package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndSpikeFeature
extends Feature<EndSpikeFeatureConfig> {
    private static final LoadingCache<Long, List<EndSpike>> LOADING_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new EndSpikeCacheLoader());

    public EndSpikeFeature(Codec<EndSpikeFeatureConfig> p_i231994_1_) {
        super(p_i231994_1_);
    }

    public static List<EndSpike> func_236356_a_(ISeedReader p_236356_0_) {
        Random random = new Random(p_236356_0_.getSeed());
        long i = random.nextLong() & 0xFFFFL;
        return LOADING_CACHE.getUnchecked(i);
    }

    @Override
    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, EndSpikeFeatureConfig p_241855_5_) {
        List<EndSpike> list = p_241855_5_.getSpikes();
        if (list.isEmpty()) {
            list = EndSpikeFeature.func_236356_a_(p_241855_1_);
        }
        for (EndSpike endspikefeature$endspike : list) {
            if (!endspikefeature$endspike.doesStartInChunk(p_241855_4_)) continue;
            this.placeSpike(p_241855_1_, p_241855_3_, p_241855_5_, endspikefeature$endspike);
        }
        return true;
    }

    private void placeSpike(IServerWorld worldIn, Random rand, EndSpikeFeatureConfig config, EndSpike spike) {
        int i = spike.getRadius();
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(spike.getCenterX() - i, 0, spike.getCenterZ() - i), new BlockPos(spike.getCenterX() + i, spike.getHeight() + 10, spike.getCenterZ() + i))) {
            if (blockpos.distanceSq(spike.getCenterX(), blockpos.getY(), spike.getCenterZ(), false) <= (double)(i * i + 1) && blockpos.getY() < spike.getHeight()) {
                this.setBlockState(worldIn, blockpos, Blocks.OBSIDIAN.getDefaultState());
                continue;
            }
            if (blockpos.getY() <= 65) continue;
            this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
        }
        if (spike.isGuarded()) {
            int j1 = -2;
            int k1 = 2;
            int j = 3;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            for (int k = -2; k <= 2; ++k) {
                for (int l = -2; l <= 2; ++l) {
                    for (int i1 = 0; i1 <= 3; ++i1) {
                        boolean flag2;
                        boolean flag = MathHelper.abs(k) == 2;
                        boolean flag1 = MathHelper.abs(l) == 2;
                        boolean bl = flag2 = i1 == 3;
                        if (!flag && !flag1 && !flag2) continue;
                        boolean flag3 = k == -2 || k == 2 || flag2;
                        boolean flag4 = l == -2 || l == 2 || flag2;
                        BlockState blockstate = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, flag3 && l != -2)).with(PaneBlock.SOUTH, flag3 && l != 2)).with(PaneBlock.WEST, flag4 && k != -2)).with(PaneBlock.EAST, flag4 && k != 2);
                        this.setBlockState(worldIn, blockpos$mutable.setPos(spike.getCenterX() + k, spike.getHeight() + i1, spike.getCenterZ() + l), blockstate);
                    }
                }
            }
        }
        EnderCrystalEntity endercrystalentity = EntityType.END_CRYSTAL.create(worldIn.getWorld());
        endercrystalentity.setBeamTarget(config.getCrystalBeamTarget());
        endercrystalentity.setInvulnerable(config.isCrystalInvulnerable());
        endercrystalentity.setLocationAndAngles((double)spike.getCenterX() + 0.5, spike.getHeight() + 1, (double)spike.getCenterZ() + 0.5, rand.nextFloat() * 360.0f, 0.0f);
        worldIn.addEntity(endercrystalentity);
        this.setBlockState(worldIn, new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ()), Blocks.BEDROCK.getDefaultState());
    }

    public static class EndSpike {
        public static final Codec<EndSpike> field_236357_a_ = RecordCodecBuilder.create(p_236359_0_ -> p_236359_0_.group(((MapCodec)Codec.INT.fieldOf("centerX")).orElse(0).forGetter(p_236363_0_ -> p_236363_0_.centerX), ((MapCodec)Codec.INT.fieldOf("centerZ")).orElse(0).forGetter(p_236362_0_ -> p_236362_0_.centerZ), ((MapCodec)Codec.INT.fieldOf("radius")).orElse(0).forGetter(p_236361_0_ -> p_236361_0_.radius), ((MapCodec)Codec.INT.fieldOf("height")).orElse(0).forGetter(p_236360_0_ -> p_236360_0_.height), ((MapCodec)Codec.BOOL.fieldOf("guarded")).orElse(false).forGetter(p_236358_0_ -> p_236358_0_.guarded)).apply((Applicative<EndSpike, ?>)p_236359_0_, EndSpike::new));
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final AxisAlignedBB topBoundingBox;

        public EndSpike(int centerXIn, int centerZIn, int radiusIn, int heightIn, boolean guardedIn) {
            this.centerX = centerXIn;
            this.centerZ = centerZIn;
            this.radius = radiusIn;
            this.height = heightIn;
            this.guarded = guardedIn;
            this.topBoundingBox = new AxisAlignedBB(centerXIn - radiusIn, 0.0, centerZIn - radiusIn, centerXIn + radiusIn, 256.0, centerZIn + radiusIn);
        }

        public boolean doesStartInChunk(BlockPos pos) {
            return pos.getX() >> 4 == this.centerX >> 4 && pos.getZ() >> 4 == this.centerZ >> 4;
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public AxisAlignedBB getTopBoundingBox() {
            return this.topBoundingBox;
        }
    }

    static class EndSpikeCacheLoader
    extends CacheLoader<Long, List<EndSpike>> {
        private EndSpikeCacheLoader() {
        }

        @Override
        public List<EndSpike> load(Long p_load_1_) {
            List list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Collections.shuffle(list, new Random(p_load_1_));
            ArrayList<EndSpike> list1 = Lists.newArrayList();
            for (int i = 0; i < 10; ++i) {
                int j = MathHelper.floor(42.0 * Math.cos(2.0 * (-Math.PI + 0.3141592653589793 * (double)i)));
                int k = MathHelper.floor(42.0 * Math.sin(2.0 * (-Math.PI + 0.3141592653589793 * (double)i)));
                int l = (Integer)list.get(i);
                int i1 = 2 + l / 3;
                int j1 = 76 + l * 3;
                boolean flag = l == 1 || l == 2;
                list1.add(new EndSpike(j, k, i1, j1, flag));
            }
            return list1;
        }
    }
}
