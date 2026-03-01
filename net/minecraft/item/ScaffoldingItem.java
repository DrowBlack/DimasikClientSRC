package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ScaffoldingItem
extends BlockItem {
    public ScaffoldingItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Override
    @Nullable
    public BlockItemUseContext getBlockItemUseContext(BlockItemUseContext context) {
        Block block;
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(blockpos);
        if (!blockstate.isIn(block = this.getBlock())) {
            return ScaffoldingBlock.getDistance(world, blockpos) == 7 ? null : context;
        }
        Direction direction = context.hasSecondaryUseForPlayer() ? (context.isInside() ? context.getFace().getOpposite() : context.getFace()) : (context.getFace() == Direction.UP ? context.getPlacementHorizontalFacing() : Direction.UP);
        int i = 0;
        BlockPos.Mutable blockpos$mutable = blockpos.toMutable().move(direction);
        while (i < 7) {
            if (!world.isRemote && !World.isValid(blockpos$mutable)) {
                PlayerEntity playerentity = context.getPlayer();
                int j = world.getHeight();
                if (!(playerentity instanceof ServerPlayerEntity) || blockpos$mutable.getY() < j) break;
                SChatPacket schatpacket = new SChatPacket(new TranslationTextComponent("build.tooHigh", j).mergeStyle(TextFormatting.RED), ChatType.GAME_INFO, Util.DUMMY_UUID);
                ((ServerPlayerEntity)playerentity).connection.sendPacket(schatpacket);
                break;
            }
            blockstate = world.getBlockState(blockpos$mutable);
            if (!blockstate.isIn(this.getBlock())) {
                if (!blockstate.isReplaceable(context)) break;
                return BlockItemUseContext.func_221536_a(context, blockpos$mutable, direction);
            }
            blockpos$mutable.move(direction);
            if (!direction.getAxis().isHorizontal()) continue;
            ++i;
        }
        return null;
    }

    @Override
    protected boolean checkPosition() {
        return false;
    }
}
