package dimasik.itemics.utils;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class BlockPlaceHelper
implements Helper {
    private final IPlayerContext ctx;
    private int rightClickTimer;

    BlockPlaceHelper(IPlayerContext playerContext) {
        this.ctx = playerContext;
    }

    public void tick(boolean rightClickRequested) {
        if (this.rightClickTimer > 0) {
            --this.rightClickTimer;
            return;
        }
        RayTraceResult mouseOver = this.ctx.objectMouseOver();
        if (!rightClickRequested || this.ctx.player().isRowingBoat() || mouseOver == null || mouseOver.getType() != RayTraceResult.Type.BLOCK) {
            return;
        }
        this.rightClickTimer = (Integer)Itemics.settings().rightClickSpeed.value;
        for (Hand hand : Hand.values()) {
            if (this.ctx.playerController().processRightClickBlock(this.ctx.player(), this.ctx.world(), hand, (BlockRayTraceResult)mouseOver) == ActionResultType.SUCCESS) {
                this.ctx.player().swingArm(hand);
                return;
            }
            if (this.ctx.player().getHeldItem(hand).isEmpty() || this.ctx.playerController().processRightClick(this.ctx.player(), this.ctx.world(), hand) != ActionResultType.SUCCESS) continue;
            return;
        }
    }
}
