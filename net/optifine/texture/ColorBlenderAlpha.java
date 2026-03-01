package net.optifine.texture;

import net.optifine.Mipmaps;
import net.optifine.texture.IColorBlender;

public class ColorBlenderAlpha
implements IColorBlender {
    @Override
    public int blend(int col1, int col2, int col3, int col4) {
        return Mipmaps.alphaBlend(col1, col2, col3, col4);
    }
}
