package dimasik.managers.font;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.helpers.interfaces.IShaderAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.font.FontData;
import dimasik.managers.font.MsdfFont;
import java.awt.Color;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class Font
implements IShaderAccess {
    private final MsdfFont font;

    public Font(String name) {
        this(name + ".png", name + ".json");
    }

    public Font(String atlas, String data) {
        this.font = MsdfFont.builder().withAtlas(atlas).withData(data).build();
    }

    public void drawText(MatrixStack stack, String text, float x, float y, int color, float size) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        FontData.AtlasData atlas = this.font.getAtlas();
        FONT.useProgram();
        FONT.setupUniformi("Sampler", 0);
        FONT.setupUniformf("EdgeStrength", 0.5f);
        FONT.setupUniformf("TextureSize", atlas.width(), atlas.height());
        FONT.setupUniformf("Range", atlas.range());
        FONT.setupUniformf("Thickness", 0.0f);
        FONT.setupUniformi("Outline", 0);
        FONT.setupUniformf("OutlineThickness", 0.0f);
        FONT.setupUniformf("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        FONT.setupUniformf("color", ColorHelpers.getRGBAf(color));
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, 0.0f, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        Tessellator.getInstance().draw();
        this.font.unbind();
        FONT.unloadProgram();
    }

    public void drawText(MatrixStack stack, ITextComponent text, float x, float y, float size, float alpha) {
        float offset = 0.0f;
        for (ITextComponent it : text.getSiblings()) {
            for (ITextComponent it1 : it.getSiblings()) {
                String draw = it1.getString();
                if (it1.getStyle().getColor() != null) {
                    this.drawText(stack, draw, x + offset, y, ColorHelpers.getColorWithAlpha(ColorHelpers.hexToRgb(it1.getStyle().getColor().getHex()), alpha), size);
                } else {
                    this.drawText(stack, draw, x + offset, y, ColorHelpers.getColorWithAlpha(Color.GRAY.getRGB(), alpha), size);
                }
                offset += this.getWidth(draw, size);
            }
            if (it.getSiblings().size() > 1) continue;
            String draw = TextFormatting.getTextWithoutFormattingCodes(it.getString());
            this.drawText(stack, draw, x + offset, y, ColorHelpers.getColorWithAlpha(it.getStyle().getColor() == null ? Color.GRAY.getRGB() : it.getStyle().getColor().getColor(), alpha), size);
            offset += this.getWidth(draw, size);
        }
        if (text.getSiblings().isEmpty()) {
            String draw = TextFormatting.getTextWithoutFormattingCodes(text.getString());
            this.drawText(stack, draw, x + offset, y, ColorHelpers.getColorWithAlpha(text.getStyle().getColor() == null ? Color.GRAY.getRGB() : text.getStyle().getColor().getColor(), alpha), size);
            offset += this.getWidth(draw, size);
        }
    }

    public float getWidth(ITextComponent text, float size) {
        float offset = 0.0f;
        for (ITextComponent it : text.getSiblings()) {
            for (ITextComponent it1 : it.getSiblings()) {
                String draw = it1.getString();
                offset += this.getWidth(draw, size);
            }
            if (it.getSiblings().size() > 1) continue;
            String draw = TextFormatting.getTextWithoutFormattingCodes(it.getString());
            offset += this.getWidth(draw, size);
        }
        if (text.getSiblings().isEmpty()) {
            String draw = TextFormatting.getTextWithoutFormattingCodes(text.getString());
            offset += this.getWidth(draw, size);
        }
        return offset;
    }

    public void drawTextBuilding(MatrixStack stack, String text, float x, float y, int color, float size) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        FontData.AtlasData atlas = this.font.getAtlas();
        FONT.useProgram();
        FONT.setupUniformi("Sampler", 0);
        FONT.setupUniformf("EdgeStrength", 0.5f);
        FONT.setupUniformf("TextureSize", atlas.width(), atlas.height());
        FONT.setupUniformf("Range", atlas.range());
        FONT.setupUniformf("Thickness", 0.0f);
        FONT.setupUniformi("Outline", 0);
        FONT.setupUniformf("OutlineThickness", 0.0f);
        FONT.setupUniformf("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        FONT.setupUniformf("color", ColorHelpers.getRGBAf(color));
        this.font.bind();
        GlStateManager.enableBlend();
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, 0.0f, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        this.font.unbind();
        FONT.unloadProgram();
    }

    public void drawCenteredText(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawText(stack, text, x - this.getWidth(text, size) / 2.0f, y, color, size);
    }

    public void drawCenteredText(MatrixStack stack, ITextComponent text, float x, float y, float size, float alpha) {
        this.drawText(stack, text, x - this.getWidth(text, size) / 2.0f, y, size, alpha);
    }

    public void drawCenteredTextWithOutline(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawTextWithOutline(stack, text, x - this.getWidth(text, size) / 2.0f, y, color, size, 0.05f);
    }

    public void drawCenteredTextEmpty(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawEmpty(stack, text, x - this.getWidth(text, size) / 2.0f, y, size, color, 0.0f);
    }

    public void drawCenteredTextEmptyOutline(MatrixStack stack, String text, float x, float y, int color, float size) {
        this.drawEmptyWithOutline(stack, text, x - this.getWidth(text, size) / 2.0f, y, size, color, 0.0f);
    }

    public void drawCenteredText(MatrixStack stack, String text, float x, float y, int color, float size, float thickness) {
        this.drawText(stack, text, x - this.getWidth(text, size, thickness) / 2.0f, y, color, size, thickness);
    }

    public void drawText(MatrixStack stack, String text, float x, float y, int color, float size, float thickness) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        FontData.AtlasData atlas = this.font.getAtlas();
        FONT.useProgram();
        FONT.setupUniformi("Sampler", 0);
        FONT.setupUniformf("EdgeStrength", 0.5f);
        FONT.setupUniformf("TextureSize", atlas.width(), atlas.height());
        FONT.setupUniformf("Range", atlas.range());
        FONT.setupUniformf("Thickness", thickness);
        FONT.setupUniformf("color", ColorHelpers.getRGBAf(color));
        FONT.setupUniformi("Outline", 0);
        FONT.setupUniformf("OutlineThickness", 0.0f);
        FONT.setupUniformf("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        Tessellator.getInstance().draw();
        this.font.unbind();
        FONT.unloadProgram();
    }

    public void drawTextWithOutline(MatrixStack stack, String text, float x, float y, int color, float size, float thickness) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        FontData.AtlasData atlas = this.font.getAtlas();
        FONT.useProgram();
        FONT.setupUniformi("Sampler", 0);
        FONT.setupUniformf("EdgeStrength", 0.5f);
        FONT.setupUniformf("TextureSize", atlas.width(), atlas.height());
        FONT.setupUniformf("Range", atlas.range());
        FONT.setupUniformf("Thickness", thickness);
        FONT.setupUniformf("color", ColorHelpers.getRGBAf(color));
        FONT.setupUniformi("Outline", 1);
        FONT.setupUniformf("OutlineThickness", 0.2f);
        FONT.setupUniformf("OutlineColor", 0.0f, 0.0f, 0.0f, 1.0f);
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
        Tessellator.getInstance().draw();
        this.font.unbind();
        FONT.unloadProgram();
    }

    public void init(float thickness, float smoothness) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        FontData.AtlasData atlas = this.font.getAtlas();
        FONT.useProgram();
        FONT.setupUniformi("Sampler", 0);
        FONT.setupUniformf("EdgeStrength", smoothness);
        FONT.setupUniformf("TextureSize", atlas.width(), atlas.height());
        FONT.setupUniformf("Range", atlas.range());
        FONT.setupUniformf("Thickness", thickness);
        FONT.setupUniformi("Outline", 0);
        FONT.setupUniformf("OutlineThickness", 0.0f);
        FONT.setupUniformf("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        this.font.bind();
        GlStateManager.enableBlend();
        Tessellator.getInstance().getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
    }

    public void drawEmpty(MatrixStack stack, String text, float x, float y, float size, int color, float thickness) {
        FONT.setupUniformf("color", ColorHelpers.getRGBAf(color));
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
    }

    public void drawEmptyWithOutline(MatrixStack stack, String text, float x, float y, float size, int color, float thickness) {
        FONT.setupUniformi("Outline", 1);
        FONT.setupUniformf("OutlineThickness", 0.2f);
        FONT.setupUniformf("OutlineColor", 0.0f, 0.0f, 0.0f, 1.0f);
        FONT.setupUniformf("color", ColorHelpers.getRGBAf(color));
        this.font.applyGlyphs(stack.getLast().getMatrix(), Tessellator.getInstance().getBuffer(), size, text, thickness, x, y + this.font.getMetrics().baselineHeight() * size, 0.0f, 255, 255, 255, 255);
    }

    public void end() {
        Tessellator.getInstance().draw();
        this.font.unbind();
        FONT.unloadProgram();
    }

    public float getWidth(String text, float size) {
        return this.font.getWidth(text, size);
    }

    public float getWidth(String text, float size, float thickness) {
        return this.font.getWidth(text, size, thickness);
    }

    public float getHeight(float size) {
        return size;
    }
}
