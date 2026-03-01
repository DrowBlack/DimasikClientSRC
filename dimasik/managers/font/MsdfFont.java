package dimasik.managers.font;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dimasik.managers.font.FontData;
import dimasik.managers.font.IOUtils;
import dimasik.managers.font.MsdfGlyph;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public final class MsdfFont {
    private final String name;
    private final Texture texture;
    private final FontData.AtlasData atlas;
    private final FontData.MetricsData metrics;
    private final Map<Integer, MsdfGlyph> glyphs;
    private boolean filtered = false;

    private MsdfFont(String name, Texture texture, FontData.AtlasData atlas, FontData.MetricsData metrics, Map<Integer, MsdfGlyph> glyphs) {
        this.name = name;
        this.texture = texture;
        this.atlas = atlas;
        this.metrics = metrics;
        this.glyphs = glyphs;
    }

    public void bind() {
        GlStateManager.bindTexture(this.texture.getGlTextureId());
        if (!this.filtered) {
            this.texture.setBlurMipmapDirect(true, false);
            this.filtered = true;
        }
    }

    public void unbind() {
        GlStateManager.bindTexture(0);
    }

    public void applyGlyphs(Matrix4f matrix, IVertexBuilder processor, float size, String text, float thickness, float x, float y, float z, int red, int green, int blue, int alpha) {
        for (int i = 0; i < text.length(); ++i) {
            char _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;
            x += glyph.apply(matrix, processor, size, x, y, z, red, green, blue, alpha) + thickness;
        }
    }

    public float getWidth(String text, float size) {
        float width = 0.0f;
        for (int i = 0; i < text.length(); ++i) {
            char _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;
            width += glyph.getWidth(size);
        }
        return width;
    }

    public float getWidth(String text, float size, float thickness) {
        float width = 0.0f;
        for (int i = 0; i < text.length(); ++i) {
            char _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;
            width += glyph.getWidth(size) + thickness;
        }
        return width;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public FontData.AtlasData getAtlas() {
        return this.atlas;
    }

    @Generated
    public FontData.MetricsData getMetrics() {
        return this.metrics;
    }

    public static class Builder {
        public static final String MSDF_PATH = "textures/fonts/";
        private String name = "undefined";
        private ResourceLocation dataFile;
        private ResourceLocation atlasFile;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withData(String dataFile) {
            this.dataFile = new ResourceLocation("main/textures/fonts/" + dataFile);
            return this;
        }

        public Builder withAtlas(String atlasFile) {
            this.atlasFile = new ResourceLocation("main/textures/fonts/" + atlasFile);
            return this;
        }

        public MsdfFont build() {
            FontData data = IOUtils.fromJsonToInstance(this.dataFile, FontData.class);
            Texture texture = IOUtils.toTexture(this.atlasFile);
            if (data == null) {
                throw new RuntimeException("Failed to read font data file: " + this.dataFile.toString() + "; Are you sure this is json file? Try to check the correctness of its syntax.");
            }
            float aWidth = data.atlas().width();
            float aHeight = data.atlas().height();
            Map<Integer, MsdfGlyph> glyphs = data.glyphs().stream().collect(Collectors.toMap(glyphData -> glyphData.unicode(), glyphData -> new MsdfGlyph((FontData.GlyphData)glyphData, aWidth, aHeight)));
            return new MsdfFont(this.name, texture, data.atlas(), data.metrics(), glyphs);
        }
    }
}
