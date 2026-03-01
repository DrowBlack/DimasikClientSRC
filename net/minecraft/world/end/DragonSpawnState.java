package net.minecraft.world.end;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum DragonSpawnState {
    START{

        @Override
        public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
            BlockPos blockpos = new BlockPos(0, 128, 0);
            for (EnderCrystalEntity endercrystalentity : crystals) {
                endercrystalentity.setBeamTarget(blockpos);
            }
            manager.setRespawnState(PREPARING_TO_SUMMON_PILLARS);
        }
    }
    ,
    PREPARING_TO_SUMMON_PILLARS{

        @Override
        public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
            if (ticks < 100) {
                if (ticks == 0 || ticks == 50 || ticks == 51 || ticks == 52 || ticks >= 95) {
                    worldIn.playEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            } else {
                manager.setRespawnState(SUMMONING_PILLARS);
            }
        }
    }
    ,
    SUMMONING_PILLARS{

        @Override
        public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
            boolean flag1;
            int i = 40;
            boolean flag = ticks % 40 == 0;
            boolean bl = flag1 = ticks % 40 == 39;
            if (flag || flag1) {
                int j = ticks / 40;
                List<EndSpikeFeature.EndSpike> list = EndSpikeFeature.func_236356_a_(worldIn);
                if (j < list.size()) {
                    EndSpikeFeature.EndSpike endspikefeature$endspike = list.get(j);
                    if (flag) {
                        for (EnderCrystalEntity endercrystalentity : crystals) {
                            endercrystalentity.setBeamTarget(new BlockPos(endspikefeature$endspike.getCenterX(), endspikefeature$endspike.getHeight() + 1, endspikefeature$endspike.getCenterZ()));
                        }
                    } else {
                        int k = 10;
                        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(endspikefeature$endspike.getCenterX() - 10, endspikefeature$endspike.getHeight() - 10, endspikefeature$endspike.getCenterZ() - 10), new BlockPos(endspikefeature$endspike.getCenterX() + 10, endspikefeature$endspike.getHeight() + 10, endspikefeature$endspike.getCenterZ() + 10))) {
                            worldIn.removeBlock(blockpos, false);
                        }
                        worldIn.createExplosion(null, (float)endspikefeature$endspike.getCenterX() + 0.5f, endspikefeature$endspike.getHeight(), (float)endspikefeature$endspike.getCenterZ() + 0.5f, 5.0f, Explosion.Mode.DESTROY);
                        EndSpikeFeatureConfig endspikefeatureconfig = new EndSpikeFeatureConfig(true, ImmutableList.of(endspikefeature$endspike), new BlockPos(0, 128, 0));
                        Feature.END_SPIKE.withConfiguration(endspikefeatureconfig).func_242765_a(worldIn, worldIn.getChunkProvider().getChunkGenerator(), new Random(), new BlockPos(endspikefeature$endspike.getCenterX(), 45, endspikefeature$endspike.getCenterZ()));
                    }
                } else if (flag) {
                    manager.setRespawnState(SUMMONING_DRAGON);
                }
            }
        }
    }
    ,
    SUMMONING_DRAGON{

        @Override
        public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
            if (ticks >= 100) {
                manager.setRespawnState(END);
                manager.resetSpikeCrystals();
                for (EnderCrystalEntity endercrystalentity : crystals) {
                    endercrystalentity.setBeamTarget(null);
                    worldIn.createExplosion(endercrystalentity, endercrystalentity.getPosX(), endercrystalentity.getPosY(), endercrystalentity.getPosZ(), 6.0f, Explosion.Mode.NONE);
                    endercrystalentity.remove();
                }
            } else if (ticks >= 80) {
                worldIn.playEvent(3001, new BlockPos(0, 128, 0), 0);
            } else if (ticks == 0) {
                for (EnderCrystalEntity endercrystalentity1 : crystals) {
                    endercrystalentity1.setBeamTarget(new BlockPos(0, 128, 0));
                }
            } else if (ticks < 5) {
                worldIn.playEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        }
    }
    ,
    END{

        @Override
        public void process(ServerWorld worldIn, DragonFightManager manager, List<EnderCrystalEntity> crystals, int ticks, BlockPos pos) {
        }
    };


    public abstract void process(ServerWorld var1, DragonFightManager var2, List<EnderCrystalEntity> var3, int var4, BlockPos var5);
}
