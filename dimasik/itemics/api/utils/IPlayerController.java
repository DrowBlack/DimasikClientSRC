package dimasik.itemics.api.utils;

import dimasik.itemics.api.ItemicsAPI;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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

public interface IPlayerController {
    public void syncHeldItem();

    public boolean hasBrokenBlock();

    public boolean onPlayerDamageBlock(BlockPos var1, Direction var2);

    public void resetBlockRemoving();

    public ItemStack windowClick(int var1, int var2, int var3, ClickType var4, PlayerEntity var5);

    public GameType getGameType();

    public ActionResultType processRightClickBlock(ClientPlayerEntity var1, World var2, Hand var3, BlockRayTraceResult var4);

    public ActionResultType processRightClick(ClientPlayerEntity var1, World var2, Hand var3);

    public boolean clickBlock(BlockPos var1, Direction var2);

    public void setHittingBlock(boolean var1);

    default public double getBlockReachDistance() {
        return this.getGameType().isCreative() ? 5.0 : (double)((Float)ItemicsAPI.getSettings().blockReachDistance.value).floatValue();
    }
}
