package dimasik.managers.font;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.font.FontData;
import net.minecraft.util.math.vector.Matrix4f;

public final class MsdfGlyph
implements IFastAccess {
    private final int code;
    private final float minU;
    private final float maxU;
    private final float minV;
    private final float maxV;
    private final float advance;
    private final float topPosition;
    private final float width;
    private final float height;

    public MsdfGlyph(FontData.GlyphData data, float atlasWidth, float atlasHeight) {
        this.code = data.unicode();
        this.advance = data.advance();
        FontData.BoundsData atlasBounds = data.atlasBounds();
        if (atlasBounds != null) {
            this.minU = atlasBounds.left() / atlasWidth;
            this.maxU = atlasBounds.right() / atlasWidth;
            this.minV = 1.0f - atlasBounds.top() / atlasHeight;
            this.maxV = 1.0f - atlasBounds.bottom() / atlasHeight;
        } else {
            this.minU = 0.0f;
            this.maxU = 0.0f;
            this.minV = 0.0f;
            this.maxV = 0.0f;
        }
        FontData.BoundsData planeBounds = data.planeBounds();
        if (planeBounds != null) {
            this.width = planeBounds.right() - planeBounds.left();
            this.height = planeBounds.top() - planeBounds.bottom();
            this.topPosition = planeBounds.top();
        } else {
            this.width = 0.0f;
            this.height = 0.0f;
            this.topPosition = 0.0f;
        }
    }

    public float apply(Matrix4f matrix, IVertexBuilder processor, float size, float x, float y, float z, int red, int green, int blue, int alpha) {
        y -= this.topPosition * size;
        float width = this.width * size;
        float height = this.height * size;
        processor.pos(matrix, x, y -= 1.0f, z).color(red, green, blue, alpha).tex(this.minU, this.minV).endVertex();
        processor.pos(matrix, x, y + height, z).color(red, green, blue, alpha).tex(this.minU, this.maxV).endVertex();
        processor.pos(matrix, x + width, y + height, z).color(red, green, blue, alpha).tex(this.maxU, this.maxV).endVertex();
        processor.pos(matrix, x + width, y, z).color(red, green, blue, alpha).tex(this.maxU, this.minV).endVertex();
        return this.width * (size - 1.0f) + (Character.isSpaceChar(this.code) ? this.advance * size : 0.0f);
    }

    public float getWidth(float size) {
        return this.width * (size - 1.0f) + (Character.isSpaceChar(this.code) ? this.advance * size : 0.0f);
    }

    public int getCharCode() {
        return this.code;
    }
}
