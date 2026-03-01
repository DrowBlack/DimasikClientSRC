package net.optifine.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.render.VertexPosition;

public abstract class VertexBuilderWrapper
implements IVertexBuilder {
    private IVertexBuilder vertexBuilder;

    public VertexBuilderWrapper(IVertexBuilder vertexBuilder) {
        this.vertexBuilder = vertexBuilder;
    }

    public IVertexBuilder getVertexBuilder() {
        return this.vertexBuilder;
    }

    @Override
    public void putSprite(TextureAtlasSprite sprite) {
        this.vertexBuilder.putSprite(sprite);
    }

    @Override
    public void setSprite(TextureAtlasSprite sprite) {
        this.vertexBuilder.setSprite(sprite);
    }

    @Override
    public boolean isMultiTexture() {
        return this.vertexBuilder.isMultiTexture();
    }

    @Override
    public void setRenderType(RenderType renderType) {
        this.vertexBuilder.setRenderType(renderType);
    }

    @Override
    public RenderType getRenderType() {
        return this.vertexBuilder.getRenderType();
    }

    @Override
    public void setRenderBlocks(boolean renderBlocks) {
        this.vertexBuilder.setRenderBlocks(renderBlocks);
    }

    @Override
    public Vector3f getTempVec3f(Vector3f vec) {
        return this.vertexBuilder.getTempVec3f(vec);
    }

    @Override
    public Vector3f getTempVec3f(float x, float y, float z) {
        return this.vertexBuilder.getTempVec3f(x, y, z);
    }

    @Override
    public float[] getTempFloat4(float f1, float f2, float f3, float f4) {
        return this.vertexBuilder.getTempFloat4(f1, f2, f3, f4);
    }

    @Override
    public int[] getTempInt4(int i1, int i2, int i3, int i4) {
        return this.vertexBuilder.getTempInt4(i1, i2, i3, i4);
    }

    @Override
    public IRenderTypeBuffer.Impl getRenderTypeBuffer() {
        return this.vertexBuilder.getRenderTypeBuffer();
    }

    @Override
    public void setQuadVertexPositions(VertexPosition[] vps) {
        this.vertexBuilder.setQuadVertexPositions(vps);
    }

    @Override
    public void setMidBlock(float mbx, float mby, float mbz) {
        this.vertexBuilder.setMidBlock(mbx, mby, mbz);
    }
}
