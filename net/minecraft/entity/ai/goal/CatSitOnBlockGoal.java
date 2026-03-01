package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatSitOnBlockGoal
extends MoveToBlockGoal {
    private final CatEntity cat;

    public CatSitOnBlockGoal(CatEntity cat, double speed) {
        super(cat, speed, 8);
        this.cat = cat;
    }

    @Override
    public boolean shouldExecute() {
        return this.cat.isTamed() && !this.cat.isSitting() && super.shouldExecute();
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.cat.setSleeping(false);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.cat.setSleeping(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.cat.setSleeping(this.getIsAboveDestination());
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        if (!worldIn.isAirBlock(pos.up())) {
            return false;
        }
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.isIn(Blocks.CHEST)) {
            return ChestTileEntity.getPlayersUsing(worldIn, pos) < 1;
        }
        return blockstate.isIn(Blocks.FURNACE) && blockstate.get(FurnaceBlock.LIT) != false ? true : blockstate.isInAndMatches(BlockTags.BEDS, state -> state.func_235903_d_(BedBlock.PART).map(bedPart -> bedPart != BedPart.HEAD).orElse(true));
    }
}
