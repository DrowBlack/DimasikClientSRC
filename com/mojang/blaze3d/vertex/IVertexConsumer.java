package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.MathHelper;

public interface IVertexConsumer
extends IVertexBuilder {
    public VertexFormatElement getCurrentElement();

    public void nextVertexFormatIndex();

    public void putByte(int var1, byte var2);

    public void putShort(int var1, short var2);

    public void putFloat(int var1, float var2);

    @Override
    default public IVertexBuilder pos(double x, double y, double z) {
        if (this.getCurrentElement().getType() != VertexFormatElement.Type.FLOAT) {
            throw new IllegalStateException();
        }
        this.putFloat(0, (float)x);
        this.putFloat(4, (float)y);
        this.putFloat(8, (float)z);
        this.nextVertexFormatIndex();
        return this;
    }

    @Override
    default public IVertexBuilder color(int red, int green, int blue, int alpha) {
        VertexFormatElement vertexformatelement = this.getCurrentElement();
        if (vertexformatelement.getUsage() != VertexFormatElement.Usage.COLOR) {
            return this;
        }
        if (vertexformatelement.getType() != VertexFormatElement.Type.UBYTE) {
            throw new IllegalStateException();
        }
        this.putByte(0, (byte)red);
        this.putByte(1, (byte)green);
        this.putByte(2, (byte)blue);
        this.putByte(3, (byte)alpha);
        this.nextVertexFormatIndex();
        return this;
    }

    @Override
    default public IVertexBuilder tex(float u, float v) {
        VertexFormatElement vertexformatelement = this.getCurrentElement();
        if (vertexformatelement.getUsage() == VertexFormatElement.Usage.UV && vertexformatelement.getIndex() == 0) {
            if (vertexformatelement.getType() != VertexFormatElement.Type.FLOAT) {
                throw new IllegalStateException();
            }
            this.putFloat(0, u);
            this.putFloat(4, v);
            this.nextVertexFormatIndex();
            return this;
        }
        return this;
    }

    @Override
    default public IVertexBuilder overlay(int u, int v) {
        return this.texShort((short)u, (short)v, 1);
    }

    @Override
    default public IVertexBuilder lightmap(int u, int v) {
        return this.texShort((short)u, (short)v, 2);
    }

    default public IVertexBuilder texShort(short u, short v, int index) {
        VertexFormatElement vertexformatelement = this.getCurrentElement();
        if (vertexformatelement.getUsage() == VertexFormatElement.Usage.UV && vertexformatelement.getIndex() == index) {
            if (vertexformatelement.getType() != VertexFormatElement.Type.SHORT) {
                throw new IllegalStateException();
            }
            this.putShort(0, u);
            this.putShort(2, v);
            this.nextVertexFormatIndex();
            return this;
        }
        return this;
    }

    @Override
    default public IVertexBuilder normal(float x, float y, float z) {
        VertexFormatElement vertexformatelement = this.getCurrentElement();
        if (vertexformatelement.getUsage() != VertexFormatElement.Usage.NORMAL) {
            return this;
        }
        if (vertexformatelement.getType() != VertexFormatElement.Type.BYTE) {
            throw new IllegalStateException();
        }
        this.putByte(0, IVertexConsumer.normalInt(x));
        this.putByte(1, IVertexConsumer.normalInt(y));
        this.putByte(2, IVertexConsumer.normalInt(z));
        this.nextVertexFormatIndex();
        return this;
    }

    public static byte normalInt(float num) {
        return (byte)((int)(MathHelper.clamp(num, -1.0f, 1.0f) * 127.0f) & 0xFF);
    }
}
