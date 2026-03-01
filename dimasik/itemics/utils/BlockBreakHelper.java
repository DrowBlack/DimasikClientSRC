package dimasik.itemics.utils;

import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public final class BlockBreakHelper
implements Helper {
    private final IPlayerContext ctx;
    private boolean didBreakLastTick;

    BlockBreakHelper(IPlayerContext ctx) {
        this.ctx = ctx;
    }

    public void stopBreakingBlock() {
        if (this.ctx.player() != null && this.didBreakLastTick) {
            if (!this.ctx.playerController().hasBrokenBlock()) {
                this.ctx.playerController().setHittingBlock(true);
            }
            this.ctx.playerController().resetBlockRemoving();
            this.didBreakLastTick = false;
        }
    }

    public void tick(boolean isLeftClick) {
        boolean isBlockTrace;
        RayTraceResult trace = this.ctx.objectMouseOver();
        boolean bl = isBlockTrace = trace != null && trace.getType() == RayTraceResult.Type.BLOCK;
        if (isLeftClick && isBlockTrace) {
            if (!this.didBreakLastTick) {
                this.ctx.playerController().syncHeldItem();
                this.ctx.playerController().clickBlock(((BlockRayTraceResult)trace).getPos(), ((BlockRayTraceResult)trace).getFace());
                this.ctx.player().swingArm(Hand.MAIN_HAND);
            }
            if (this.ctx.playerController().onPlayerDamageBlock(((BlockRayTraceResult)trace).getPos(), ((BlockRayTraceResult)trace).getFace())) {
                this.ctx.player().swingArm(Hand.MAIN_HAND);
            }
            this.ctx.playerController().setHittingBlock(false);
            this.didBreakLastTick = true;
        } else if (this.didBreakLastTick) {
            this.stopBreakingBlock();
            this.didBreakLastTick = false;
        }
    }
}
