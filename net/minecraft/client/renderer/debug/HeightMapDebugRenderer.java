package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

public class HeightMapDebugRenderer
implements DebugRenderer.IDebugRenderer {
    private final Minecraft minecraft;

    public HeightMapDebugRenderer(Minecraft minecraftIn) {
        this.minecraft = minecraftIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        ClientWorld iworld = this.minecraft.world;
        RenderSystem.pushMatrix();
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        RenderSystem.enableDepthTest();
        BlockPos blockpos = new BlockPos(camX, 0.0, camZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        for (int i = -32; i <= 32; i += 16) {
            for (int j = -32; j <= 32; j += 16) {
                IChunk ichunk = iworld.getChunk(blockpos.add(i, 0, j));
                for (Map.Entry<Heightmap.Type, Heightmap> entry : ichunk.getHeightmaps()) {
                    Heightmap.Type heightmap$type = entry.getKey();
                    ChunkPos chunkpos = ichunk.getPos();
                    Vector3f vector3f = this.func_239373_a_(heightmap$type);
                    for (int k = 0; k < 16; ++k) {
                        for (int l = 0; l < 16; ++l) {
                            int i1 = chunkpos.x * 16 + k;
                            int j1 = chunkpos.z * 16 + l;
                            float f = (float)((double)((float)iworld.getHeight(heightmap$type, i1, j1) + (float)heightmap$type.ordinal() * 0.09375f) - camY);
                            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)i1 + 0.25f) - camX, f, (double)((float)j1 + 0.25f) - camZ, (double)((float)i1 + 0.75f) - camX, f + 0.09375f, (double)((float)j1 + 0.75f) - camZ, vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.0f);
                        }
                    }
                }
            }
        }
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    private Vector3f func_239373_a_(Heightmap.Type p_239373_1_) {
        switch (p_239373_1_) {
            case WORLD_SURFACE_WG: {
                return new Vector3f(1.0f, 1.0f, 0.0f);
            }
            case OCEAN_FLOOR_WG: {
                return new Vector3f(1.0f, 0.0f, 1.0f);
            }
            case WORLD_SURFACE: {
                return new Vector3f(0.0f, 0.7f, 0.0f);
            }
            case OCEAN_FLOOR: {
                return new Vector3f(0.0f, 0.0f, 0.5f);
            }
            case MOTION_BLOCKING: {
                return new Vector3f(0.0f, 0.3f, 0.3f);
            }
            case MOTION_BLOCKING_NO_LEAVES: {
                return new Vector3f(0.0f, 0.5f, 0.5f);
            }
        }
        return new Vector3f(0.0f, 0.0f, 0.0f);
    }
}
