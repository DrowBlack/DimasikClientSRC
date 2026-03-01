package dimasik.itemics.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.Settings;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.utils.accessor.IEntityRenderManager;
import java.awt.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;

public interface IRenderer {
    public static final Tessellator tessellator = Tessellator.getInstance();
    public static final BufferBuilder buffer = tessellator.getBuffer();
    public static final IEntityRenderManager renderManager = Helper.mc.getRenderManager();
    public static final Settings settings = ItemicsAPI.getSettings();

    public static void glColor(Color color, float alpha) {
        float[] colorComponents = color.getColorComponents(null);
        RenderSystem.color4f(colorComponents[0], colorComponents[1], colorComponents[2], alpha);
    }

    public static void startLines(Color color, float alpha, float lineWidth, boolean ignoreDepth) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        IRenderer.glColor(color, alpha);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);
        if (ignoreDepth) {
            RenderSystem.disableDepthTest();
        }
    }

    public static void startLines(Color color, float lineWidth, boolean ignoreDepth) {
        IRenderer.startLines(color, 0.4f, lineWidth, ignoreDepth);
    }

    public static void endLines(boolean ignoredDepth) {
        if (ignoredDepth) {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawAABB(MatrixStack stack, AxisAlignedBB aabb) {
        AxisAlignedBB toDraw = aabb.offset(-renderManager.renderPosX(), -renderManager.renderPosY(), -renderManager.renderPosZ());
        Matrix4f matrix4f = stack.getLast().getMatrix();
        buffer.begin(1, DefaultVertexFormats.POSITION);
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.minY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.minY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.minY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.minY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.minY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.minY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.minY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.minY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.maxY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.maxY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.maxY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.maxY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.maxY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.maxY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.maxY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.maxY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.minY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.maxY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.minY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.maxY, (float)toDraw.minZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.minY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.maxX, (float)toDraw.maxY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.minY, (float)toDraw.maxZ).endVertex();
        buffer.pos(matrix4f, (float)toDraw.minX, (float)toDraw.maxY, (float)toDraw.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawAABB(MatrixStack stack, AxisAlignedBB aabb, double expand) {
        IRenderer.drawAABB(stack, aabb.grow(expand, expand, expand));
    }
}
