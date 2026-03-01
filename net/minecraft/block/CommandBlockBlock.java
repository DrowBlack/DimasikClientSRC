package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlockBlock
extends ContainerBlock {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;

    public CommandBlockBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(CONDITIONAL, false));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        CommandBlockTileEntity commandblocktileentity = new CommandBlockTileEntity();
        commandblocktileentity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
        return commandblocktileentity;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        TileEntity tileentity;
        if (!worldIn.isRemote && (tileentity = worldIn.getTileEntity(pos)) instanceof CommandBlockTileEntity) {
            CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
            boolean flag = worldIn.isBlockPowered(pos);
            boolean flag1 = commandblocktileentity.isPowered();
            commandblocktileentity.setPowered(flag);
            if (!flag1 && !commandblocktileentity.isAuto() && commandblocktileentity.getMode() != CommandBlockTileEntity.Mode.SEQUENCE && flag) {
                commandblocktileentity.setConditionMet();
                worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof CommandBlockTileEntity) {
            CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
            CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlockLogic();
            boolean flag = !StringUtils.isNullOrEmpty(commandblocklogic.getCommand());
            CommandBlockTileEntity.Mode commandblocktileentity$mode = commandblocktileentity.getMode();
            boolean flag1 = commandblocktileentity.isConditionMet();
            if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.AUTO) {
                commandblocktileentity.setConditionMet();
                if (flag1) {
                    this.execute(state, worldIn, pos, commandblocklogic, flag);
                } else if (commandblocktileentity.isConditional()) {
                    commandblocklogic.setSuccessCount(0);
                }
                if (commandblocktileentity.isPowered() || commandblocktileentity.isAuto()) {
                    worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
                }
            } else if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.REDSTONE) {
                if (flag1) {
                    this.execute(state, worldIn, pos, commandblocklogic, flag);
                } else if (commandblocktileentity.isConditional()) {
                    commandblocklogic.setSuccessCount(0);
                }
            }
            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    private void execute(BlockState state, World world, BlockPos pos, CommandBlockLogic logic, boolean canTrigger) {
        if (canTrigger) {
            logic.trigger(world);
        } else {
            logic.setSuccessCount(0);
        }
        CommandBlockBlock.executeChain(world, pos, state.get(FACING));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof CommandBlockTileEntity && player.canUseCommandBlock()) {
            player.openCommandBlock((CommandBlockTileEntity)tileentity);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof CommandBlockTileEntity ? ((CommandBlockTileEntity)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof CommandBlockTileEntity) {
            CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
            CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlockLogic();
            if (stack.hasDisplayName()) {
                commandblocklogic.setName(stack.getDisplayName());
            }
            if (!worldIn.isRemote) {
                if (stack.getChildTag("BlockEntityTag") == null) {
                    commandblocklogic.setTrackOutput(worldIn.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
                    commandblocktileentity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
                }
                if (commandblocktileentity.getMode() == CommandBlockTileEntity.Mode.SEQUENCE) {
                    boolean flag = worldIn.isBlockPowered(pos);
                    commandblocktileentity.setPowered(flag);
                }
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONDITIONAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return (BlockState)this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
    }

    private static void executeChain(World world, BlockPos pos, Direction direction) {
        BlockPos.Mutable blockpos$mutable = pos.toMutable();
        GameRules gamerules = world.getGameRules();
        int i = gamerules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
        while (i-- > 0) {
            CommandBlockTileEntity commandblocktileentity;
            TileEntity tileentity;
            blockpos$mutable.move(direction);
            BlockState blockstate = world.getBlockState(blockpos$mutable);
            Block block = blockstate.getBlock();
            if (!blockstate.isIn(Blocks.CHAIN_COMMAND_BLOCK) || !((tileentity = world.getTileEntity(blockpos$mutable)) instanceof CommandBlockTileEntity) || (commandblocktileentity = (CommandBlockTileEntity)tileentity).getMode() != CommandBlockTileEntity.Mode.SEQUENCE) break;
            if (commandblocktileentity.isPowered() || commandblocktileentity.isAuto()) {
                CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlockLogic();
                if (commandblocktileentity.setConditionMet()) {
                    if (!commandblocklogic.trigger(world)) break;
                    world.updateComparatorOutputLevel(blockpos$mutable, block);
                } else if (commandblocktileentity.isConditional()) {
                    commandblocklogic.setSuccessCount(0);
                }
            }
            direction = blockstate.get(FACING);
        }
        if (i <= 0) {
            int j = Math.max(gamerules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
            LOGGER.warn("Command Block chain tried to execute more than {} steps!", (Object)j);
        }
    }
}
