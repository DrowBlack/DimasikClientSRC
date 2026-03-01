package net.optifine.util;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkSection;

public class RenderChunkUtils {
    public static int getCountBlocks(ChunkRenderDispatcher.ChunkRender renderChunk) {
        ChunkSection[] achunksection = renderChunk.getChunk().getSections();
        if (achunksection == null) {
            return 0;
        }
        int i = renderChunk.getPosition().getY() >> 4;
        ChunkSection chunksection = achunksection[i];
        return chunksection == null ? (short)0 : chunksection.getBlockRefCount();
    }

    public static double getRelativeBufferSize(ChunkRenderDispatcher.ChunkRender renderChunk) {
        int i = RenderChunkUtils.getCountBlocks(renderChunk);
        return RenderChunkUtils.getRelativeBufferSize(i);
    }

    public static double getRelativeBufferSize(int blockCount) {
        double d0 = (double)blockCount / 4096.0;
        double d1 = (d0 *= 0.995) * 2.0 - 1.0;
        d1 = MathHelper.clamp(d1, -1.0, 1.0);
        return MathHelper.sqrt(1.0 - d1 * d1);
    }
}
