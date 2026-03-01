package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;
import net.optifine.Config;
import net.optifine.model.BakedQuadRetextured;
import net.optifine.model.QuadBounds;
import net.optifine.reflect.Reflector;
import net.optifine.render.QuadVertexPositions;
import net.optifine.render.VertexPosition;

public class BakedQuad
implements IVertexProducer {
    protected int[] vertexData;
    protected final int tintIndex;
    protected Direction face;
    protected TextureAtlasSprite sprite;
    private final boolean applyDiffuseLighting;
    private int[] vertexDataSingle = null;
    private QuadBounds quadBounds;
    private boolean quadEmissiveChecked;
    private BakedQuad quadEmissive;
    private QuadVertexPositions quadVertexPositions;

    public BakedQuad(int[] vertexData, int tintIndex, Direction face, TextureAtlasSprite sprite, boolean applyDiffuseLighting) {
        this.vertexData = vertexData;
        this.tintIndex = tintIndex;
        this.face = face;
        this.sprite = sprite;
        this.applyDiffuseLighting = applyDiffuseLighting;
        this.fixVertexData();
    }

    public int[] getVertexData() {
        this.fixVertexData();
        return this.vertexData;
    }

    public boolean hasTintIndex() {
        return this.tintIndex != -1;
    }

    public int getTintIndex() {
        return this.tintIndex;
    }

    public Direction getFace() {
        if (this.face == null) {
            this.face = FaceBakery.getFacingFromVertexData(this.getVertexData());
        }
        return this.face;
    }

    public boolean applyDiffuseLighting() {
        return this.applyDiffuseLighting;
    }

    public TextureAtlasSprite getSprite() {
        if (this.sprite == null) {
            this.sprite = BakedQuad.getSpriteByUv(this.getVertexData());
        }
        return this.sprite;
    }

    public int[] getVertexDataSingle() {
        if (this.vertexDataSingle == null) {
            this.vertexDataSingle = BakedQuad.makeVertexDataSingle(this.getVertexData(), this.getSprite());
        }
        if (this.vertexDataSingle.length != this.getVertexData().length) {
            this.vertexDataSingle = BakedQuad.makeVertexDataSingle(this.getVertexData(), this.getSprite());
        }
        return this.vertexDataSingle;
    }

    private static int[] makeVertexDataSingle(int[] p_makeVertexDataSingle_0_, TextureAtlasSprite p_makeVertexDataSingle_1_) {
        int[] aint = (int[])p_makeVertexDataSingle_0_.clone();
        int i = aint.length / 4;
        for (int j = 0; j < 4; ++j) {
            int k = j * i;
            float f = Float.intBitsToFloat(aint[k + 4]);
            float f1 = Float.intBitsToFloat(aint[k + 4 + 1]);
            float f2 = p_makeVertexDataSingle_1_.toSingleU(f);
            float f3 = p_makeVertexDataSingle_1_.toSingleV(f1);
            aint[k + 4] = Float.floatToRawIntBits(f2);
            aint[k + 4 + 1] = Float.floatToRawIntBits(f3);
        }
        return aint;
    }

    @Override
    public void pipe(IVertexConsumer p_pipe_1_) {
        Reflector.callVoid(Reflector.LightUtil_putBakedQuad, p_pipe_1_, this);
    }

    private static TextureAtlasSprite getSpriteByUv(int[] p_getSpriteByUv_0_) {
        float f = 1.0f;
        float f1 = 1.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        int i = p_getSpriteByUv_0_.length / 4;
        for (int j = 0; j < 4; ++j) {
            int k = j * i;
            float f4 = Float.intBitsToFloat(p_getSpriteByUv_0_[k + 4]);
            float f5 = Float.intBitsToFloat(p_getSpriteByUv_0_[k + 4 + 1]);
            f = Math.min(f, f4);
            f1 = Math.min(f1, f5);
            f2 = Math.max(f2, f4);
            f3 = Math.max(f3, f5);
        }
        float f6 = (f + f2) / 2.0f;
        float f7 = (f1 + f3) / 2.0f;
        return Config.getTextureMap().getIconByUV(f6, f7);
    }

    protected void fixVertexData() {
        if (Config.isShaders()) {
            if (this.vertexData.length == DefaultVertexFormats.BLOCK_VANILLA_SIZE) {
                this.vertexData = BakedQuad.fixVertexDataSize(this.vertexData, DefaultVertexFormats.BLOCK_SHADERS_SIZE);
            }
        } else if (this.vertexData.length == DefaultVertexFormats.BLOCK_SHADERS_SIZE) {
            this.vertexData = BakedQuad.fixVertexDataSize(this.vertexData, DefaultVertexFormats.BLOCK_VANILLA_SIZE);
        }
    }

    private static int[] fixVertexDataSize(int[] p_fixVertexDataSize_0_, int p_fixVertexDataSize_1_) {
        int i = p_fixVertexDataSize_0_.length / 4;
        int j = p_fixVertexDataSize_1_ / 4;
        int[] aint = new int[j * 4];
        for (int k = 0; k < 4; ++k) {
            int l = Math.min(i, j);
            System.arraycopy(p_fixVertexDataSize_0_, k * i, aint, k * j, l);
        }
        return aint;
    }

    public QuadBounds getQuadBounds() {
        if (this.quadBounds == null) {
            this.quadBounds = new QuadBounds(this.getVertexData());
        }
        return this.quadBounds;
    }

    public float getMidX() {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxX() + quadbounds.getMinX()) / 2.0f;
    }

    public double getMidY() {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxY() + quadbounds.getMinY()) / 2.0f;
    }

    public double getMidZ() {
        QuadBounds quadbounds = this.getQuadBounds();
        return (quadbounds.getMaxZ() + quadbounds.getMinZ()) / 2.0f;
    }

    public boolean isFaceQuad() {
        QuadBounds quadbounds = this.getQuadBounds();
        return quadbounds.isFaceQuad(this.face);
    }

    public boolean isFullQuad() {
        QuadBounds quadbounds = this.getQuadBounds();
        return quadbounds.isFullQuad(this.face);
    }

    public boolean isFullFaceQuad() {
        return this.isFullQuad() && this.isFaceQuad();
    }

    public BakedQuad getQuadEmissive() {
        if (this.quadEmissiveChecked) {
            return this.quadEmissive;
        }
        if (this.quadEmissive == null && this.sprite != null && this.sprite.spriteEmissive != null) {
            this.quadEmissive = new BakedQuadRetextured(this, this.sprite.spriteEmissive);
        }
        this.quadEmissiveChecked = true;
        return this.quadEmissive;
    }

    public VertexPosition[] getVertexPositions(int p_getVertexPositions_1_) {
        if (this.quadVertexPositions == null) {
            this.quadVertexPositions = new QuadVertexPositions();
        }
        return (VertexPosition[])this.quadVertexPositions.get(p_getVertexPositions_1_);
    }

    public String toString() {
        return "vertexData: " + this.vertexData.length + ", tint: " + this.tintIndex + ", facing: " + String.valueOf(this.face) + ", sprite: " + String.valueOf(this.sprite);
    }
}
