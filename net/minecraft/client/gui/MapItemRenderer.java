package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class MapItemRenderer
implements AutoCloseable {
    private static final ResourceLocation TEXTURE_MAP_ICONS = new ResourceLocation("textures/map/map_icons.png");
    private static final RenderType field_228085_d_ = RenderType.getText(TEXTURE_MAP_ICONS);
    private final TextureManager textureManager;
    private final Map<String, Instance> loadedMaps = Maps.newHashMap();

    public MapItemRenderer(TextureManager textureManagerIn) {
        this.textureManager = textureManagerIn;
    }

    public void updateMapTexture(MapData mapdataIn) {
        this.getMapRendererInstance(mapdataIn).updateMapTexture();
    }

    public void renderMap(MatrixStack p_228086_1_, IRenderTypeBuffer p_228086_2_, MapData p_228086_3_, boolean p_228086_4_, int p_228086_5_) {
        this.getMapRendererInstance(p_228086_3_).func_228089_a_(p_228086_1_, p_228086_2_, p_228086_4_, p_228086_5_);
    }

    private Instance getMapRendererInstance(MapData mapdataIn) {
        Instance mapitemrenderer$instance = this.loadedMaps.get(mapdataIn.getName());
        if (mapitemrenderer$instance == null) {
            mapitemrenderer$instance = new Instance(mapdataIn);
            this.loadedMaps.put(mapdataIn.getName(), mapitemrenderer$instance);
        }
        return mapitemrenderer$instance;
    }

    @Nullable
    public Instance getMapInstanceIfExists(String p_191205_1_) {
        return this.loadedMaps.get(p_191205_1_);
    }

    public void clearLoadedMaps() {
        for (Instance mapitemrenderer$instance : this.loadedMaps.values()) {
            mapitemrenderer$instance.close();
        }
        this.loadedMaps.clear();
    }

    @Nullable
    public MapData getData(@Nullable Instance p_191207_1_) {
        return p_191207_1_ != null ? p_191207_1_.mapData : null;
    }

    @Override
    public void close() {
        this.clearLoadedMaps();
    }

    class Instance
    implements AutoCloseable {
        private final MapData mapData;
        private final DynamicTexture mapTexture;
        private final RenderType field_228088_d_;

        private Instance(MapData mapdataIn) {
            this.mapData = mapdataIn;
            this.mapTexture = new DynamicTexture(128, 128, true);
            ResourceLocation resourcelocation = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapdataIn.getName(), this.mapTexture);
            this.field_228088_d_ = RenderType.getText(resourcelocation);
        }

        private void updateMapTexture() {
            for (int i = 0; i < 128; ++i) {
                for (int j = 0; j < 128; ++j) {
                    int k = j + i * 128;
                    int l = this.mapData.colors[k] & 0xFF;
                    if (l / 4 == 0) {
                        this.mapTexture.getTextureData().setPixelRGBA(j, i, 0);
                        continue;
                    }
                    this.mapTexture.getTextureData().setPixelRGBA(j, i, MaterialColor.COLORS[l / 4].getMapColor(l & 3));
                }
            }
            this.mapTexture.updateDynamicTexture();
        }

        private void func_228089_a_(MatrixStack p_228089_1_, IRenderTypeBuffer p_228089_2_, boolean p_228089_3_, int p_228089_4_) {
            boolean i = false;
            boolean j = false;
            float f = 0.0f;
            Matrix4f matrix4f = p_228089_1_.getLast().getMatrix();
            IVertexBuilder ivertexbuilder = p_228089_2_.getBuffer(this.field_228088_d_);
            ivertexbuilder.pos(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).tex(0.0f, 1.0f).lightmap(p_228089_4_).endVertex();
            ivertexbuilder.pos(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).tex(1.0f, 1.0f).lightmap(p_228089_4_).endVertex();
            ivertexbuilder.pos(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).tex(1.0f, 0.0f).lightmap(p_228089_4_).endVertex();
            ivertexbuilder.pos(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).tex(0.0f, 0.0f).lightmap(p_228089_4_).endVertex();
            int k = 0;
            for (MapDecoration mapdecoration : this.mapData.mapDecorations.values()) {
                if (p_228089_3_ && !mapdecoration.renderOnFrame()) continue;
                p_228089_1_.push();
                p_228089_1_.translate(0.0f + (float)mapdecoration.getX() / 2.0f + 64.0f, 0.0f + (float)mapdecoration.getY() / 2.0f + 64.0f, -0.02f);
                p_228089_1_.rotate(Vector3f.ZP.rotationDegrees((float)(mapdecoration.getRotation() * 360) / 16.0f));
                p_228089_1_.scale(4.0f, 4.0f, 3.0f);
                p_228089_1_.translate(-0.125, 0.125, 0.0);
                byte b0 = mapdecoration.getImage();
                float f1 = (float)(b0 % 16 + 0) / 16.0f;
                float f2 = (float)(b0 / 16 + 0) / 16.0f;
                float f3 = (float)(b0 % 16 + 1) / 16.0f;
                float f4 = (float)(b0 / 16 + 1) / 16.0f;
                Matrix4f matrix4f1 = p_228089_1_.getLast().getMatrix();
                float f5 = -0.001f;
                IVertexBuilder ivertexbuilder1 = p_228089_2_.getBuffer(field_228085_d_);
                ivertexbuilder1.pos(matrix4f1, -1.0f, 1.0f, (float)k * -0.001f).color(255, 255, 255, 255).tex(f1, f2).lightmap(p_228089_4_).endVertex();
                ivertexbuilder1.pos(matrix4f1, 1.0f, 1.0f, (float)k * -0.001f).color(255, 255, 255, 255).tex(f3, f2).lightmap(p_228089_4_).endVertex();
                ivertexbuilder1.pos(matrix4f1, 1.0f, -1.0f, (float)k * -0.001f).color(255, 255, 255, 255).tex(f3, f4).lightmap(p_228089_4_).endVertex();
                ivertexbuilder1.pos(matrix4f1, -1.0f, -1.0f, (float)k * -0.001f).color(255, 255, 255, 255).tex(f1, f4).lightmap(p_228089_4_).endVertex();
                p_228089_1_.pop();
                if (mapdecoration.getCustomName() != null) {
                    FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
                    ITextComponent itextcomponent = mapdecoration.getCustomName();
                    float f6 = fontrenderer.getStringPropertyWidth(itextcomponent);
                    float f7 = MathHelper.clamp(25.0f / f6, 0.0f, 0.6666667f);
                    p_228089_1_.push();
                    p_228089_1_.translate(0.0f + (float)mapdecoration.getX() / 2.0f + 64.0f - f6 * f7 / 2.0f, 0.0f + (float)mapdecoration.getY() / 2.0f + 64.0f + 4.0f, -0.025f);
                    p_228089_1_.scale(f7, f7, 1.0f);
                    p_228089_1_.translate(0.0, 0.0, -0.1f);
                    fontrenderer.func_243247_a(itextcomponent, 0.0f, 0.0f, -1, false, p_228089_1_.getLast().getMatrix(), p_228089_2_, false, Integer.MIN_VALUE, p_228089_4_);
                    p_228089_1_.pop();
                }
                ++k;
            }
        }

        @Override
        public void close() {
            this.mapTexture.close();
        }
    }
}
