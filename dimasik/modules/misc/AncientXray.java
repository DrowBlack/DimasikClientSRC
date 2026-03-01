package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender3D;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class AncientXray
extends Module {
    private final List<BlockPos> highlightedDebris = new ArrayList<BlockPos>();
    private final List<BlockPos> clicknul = new ArrayList<BlockPos>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private BlockPos clickat = null;
    private int naydeno = 0;
    private static final int radius = 20;
    private static final int proklickzaed = 500;
    private final EventListener<EventRender3D.Post> render = this::render;

    public AncientXray() {
        super("AncientXray", Category.MISC);
    }

    private void render(EventRender3D.Post e) {
        BlockPos playerPos = Minecraft.getInstance().player.getPosition();
        this.highlightedDebris.clear();
        for (int x = -20; x <= 20; ++x) {
            for (int y = -20; y <= 20; ++y) {
                for (int z = -20; z <= 20; ++z) {
                    Block block;
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!this.isTargetBlock(pos, block = Minecraft.getInstance().world.getBlockState(pos).getBlock())) continue;
                    VisualHelpers.drawBlockBox(pos, new Color(255, 255, 255).getRGB());
                    this.highlightedDebris.add(pos);
                }
            }
        }
        if (!(this.highlightedDebris.isEmpty() || this.clickat != null && this.threadPool.isShutdown())) {
            this.startClickingThread();
        }
    }

    private boolean isTargetBlock(BlockPos pos, Block block) {
        return block == Blocks.ANCIENT_DEBRIS && this.hasAtLeastTwoAirBlocksAround(pos) && !this.hasTwoQuartzOrGoldNearby(pos) && this.hasAtLeastFiveAirInCube(pos) && !this.hasTooManyAncientDebrisNearby(pos);
    }

    private void startClickingThread() {
        this.threadPool.execute(() -> {
            for (BlockPos pos : this.highlightedDebris) {
                if (this.clicknul.contains(pos)) continue;
                Minecraft.getInstance().player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
                this.clicknul.add(pos);
                this.clickat = pos;
                ++this.naydeno;
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            this.clickat = null;
        });
    }

    private boolean hasAtLeastTwoAirBlocksAround(BlockPos pos) {
        int airBlockCount = 0;
        for (Direction direction : Direction.values()) {
            Block surroundingBlock = Minecraft.getInstance().world.getBlockState(pos.offset(direction)).getBlock();
            if (surroundingBlock != Blocks.AIR && surroundingBlock != Blocks.LAVA || ++airBlockCount < 2) continue;
            return true;
        }
        return false;
    }

    private boolean hasTwoQuartzOrGoldNearby(BlockPos pos) {
        int quartzOrGoldCount = 0;
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    Block nearbyBlock = Minecraft.getInstance().world.getBlockState(pos.add(x, y, z)).getBlock();
                    if (nearbyBlock != Blocks.NETHER_QUARTZ_ORE && nearbyBlock != Blocks.NETHER_GOLD_ORE || ++quartzOrGoldCount < 4) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAtLeastFiveAirInCube(BlockPos pos) {
        int airBlockCount = 0;
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    Block nearbyBlock = Minecraft.getInstance().world.getBlockState(pos.add(x, y, z)).getBlock();
                    if (nearbyBlock != Blocks.AIR && nearbyBlock != Blocks.LAVA || ++airBlockCount < 4) continue;
                    return true;
                }
            }
        }
        return airBlockCount >= 4;
    }

    private boolean hasTooManyAncientDebrisNearby(BlockPos pos) {
        int ancientDebrisCount = 0;
        for (int x = -3; x <= 2; ++x) {
            for (int y = -2; y <= 2; ++y) {
                for (int z = -2; z <= 3; ++z) {
                    Block nearbyBlock = Minecraft.getInstance().world.getBlockState(pos.add(x, y, z)).getBlock();
                    if (nearbyBlock != Blocks.ANCIENT_DEBRIS || ++ancientDebrisCount <= 3) continue;
                    return true;
                }
            }
        }
        return false;
    }
}
