package dimasik.itemics.utils.player;

import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerController;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public enum PrimaryPlayerController implements IPlayerController,
Helper
{
    INSTANCE;


    @Override
    public void syncHeldItem() {
        PrimaryPlayerController.mc.playerController.callSyncCurrentPlayItem();
    }

    @Override
    public boolean hasBrokenBlock() {
        return PrimaryPlayerController.mc.playerController.getCurrentBlock().getY() == -1;
    }

    @Override
    public boolean onPlayerDamageBlock(BlockPos pos, Direction side) {
        return PrimaryPlayerController.mc.playerController.onPlayerDamageBlock(pos, side);
    }

    @Override
    public void resetBlockRemoving() {
        PrimaryPlayerController.mc.playerController.resetBlockRemoving();
    }

    @Override
    public ItemStack windowClick(int windowId, int slotId, int mouseButton, ClickType type, PlayerEntity player) {
        return PrimaryPlayerController.mc.playerController.windowClick(windowId, slotId, mouseButton, type, player);
    }

    @Override
    public GameType getGameType() {
        return PrimaryPlayerController.mc.playerController.getCurrentGameType();
    }

    @Override
    public ActionResultType processRightClickBlock(ClientPlayerEntity player, World world, Hand hand, BlockRayTraceResult result) {
        return PrimaryPlayerController.mc.playerController.processRightClickBlock(player, (ClientWorld)world, hand, result);
    }

    @Override
    public ActionResultType processRightClick(ClientPlayerEntity player, World world, Hand hand) {
        return PrimaryPlayerController.mc.playerController.processRightClick(player, world, hand);
    }

    @Override
    public boolean clickBlock(BlockPos loc, Direction face) {
        return PrimaryPlayerController.mc.playerController.clickBlock(loc, face);
    }

    @Override
    public void setHittingBlock(boolean hittingBlock) {
        PrimaryPlayerController.mc.playerController.setIsHittingBlock(hittingBlock);
    }
}
