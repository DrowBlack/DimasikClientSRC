package net.optifine.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public class VertexBuilderDummy
implements IVertexBuilder {
    private IRenderTypeBuffer.Impl renderTypeBuffer = null;

    public VertexBuilderDummy(IRenderTypeBuffer.Impl renderTypeBuffer) {
        this.renderTypeBuffer = renderTypeBuffer;
    }

    @Override
    public IRenderTypeBuffer.Impl getRenderTypeBuffer() {
        return this.renderTypeBuffer;
    }

    @Override
    public IVertexBuilder pos(double x, double y, double z) {
        return this;
    }

    @Override
    public IVertexBuilder color(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public IVertexBuilder tex(float u, float v) {
        return this;
    }

    @Override
    public IVertexBuilder overlay(int u, int v) {
        return this;
    }

    @Override
    public IVertexBuilder lightmap(int u, int v) {
        return this;
    }

    @Override
    public IVertexBuilder normal(float x, float y, float z) {
        return this;
    }

    @Override
    public void endVertex() {
    }
}
