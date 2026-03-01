package dimasik.modules.render;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender3D;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.blockesp.BlockESPManagers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import net.minecraft.util.math.BlockPos;

public class BlockEsp
extends Module {
    private final EventListener<EventRender3D.Post> render = this::render;

    public BlockEsp() {
        super("BlockEsp", Category.RENDER);
    }

    public void render(EventRender3D.Post event) {
        if (BlockEsp.mc.player != null && BlockEsp.mc.world != null) {
            BlockESPManagers blockESPManagers = Load.getInstance().getHooks().getBlockESPManagers();
            blockESPManagers.updateCacheAsync(BlockEsp.mc.world, BlockEsp.mc.player.getPosition());
            for (BlockPos pos : blockESPManagers.getCachedPositions()) {
                int color = blockESPManagers.getColorFor(BlockEsp.mc.world.getBlockState(pos).getBlock());
                VisualHelpers.drawBlockBox(pos, color);
            }
        }
    }
}
