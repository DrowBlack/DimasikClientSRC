package com.jhlabs.image;

import com.jhlabs.image.PixelUtils;
import com.jhlabs.image.WholeImageFilter;
import java.awt.Rectangle;

public class DiffusionFilter
extends WholeImageFilter {
    private static final int[] diffusionMatrix;
    private int[] matrix;
    private int sum = 16;
    private boolean serpentine = true;
    private boolean colorDither = true;
    private int levels = 6;

    static {
        int[] nArray = new int[9];
        nArray[5] = 7;
        nArray[6] = 3;
        nArray[7] = 5;
        nArray[8] = 1;
        diffusionMatrix = nArray;
    }

    public DiffusionFilter() {
        this.setMatrix(diffusionMatrix);
    }

    public void setSerpentine(boolean serpentine) {
        this.serpentine = serpentine;
    }

    public boolean getSerpentine() {
        return this.serpentine;
    }

    public void setColorDither(boolean colorDither) {
        this.colorDither = colorDither;
    }

    public boolean getColorDither() {
        return this.colorDither;
    }

    public void setMatrix(int[] matrix) {
        this.matrix = matrix;
        this.sum = 0;
        int i = 0;
        while (i < matrix.length) {
            this.sum += matrix[i];
            ++i;
        }
    }

    public int[] getMatrix() {
        return this.matrix;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public int getLevels() {
        return this.levels;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int[] outPixels = new int[width * height];
        int index = 0;
        int[] map = new int[this.levels];
        int i = 0;
        while (i < this.levels) {
            int v;
            map[i] = v = 255 * i / (this.levels - 1);
            ++i;
        }
        int[] div = new int[256];
        int i2 = 0;
        while (i2 < 256) {
            div[i2] = this.levels * i2 / 256;
            ++i2;
        }
        int y = 0;
        while (y < height) {
            int direction;
            boolean reverse;
            boolean bl = reverse = this.serpentine && (y & 1) == 1;
            if (reverse) {
                index = y * width + width - 1;
                direction = -1;
            } else {
                index = y * width;
                direction = 1;
            }
            int x = 0;
            while (x < width) {
                int rgb1 = inPixels[index];
                int r1 = rgb1 >> 16 & 0xFF;
                int g1 = rgb1 >> 8 & 0xFF;
                int b1 = rgb1 & 0xFF;
                if (!this.colorDither) {
                    g1 = b1 = (r1 + g1 + b1) / 3;
                    r1 = b1;
                }
                int r2 = map[div[r1]];
                int g2 = map[div[g1]];
                int b2 = map[div[b1]];
                outPixels[index] = rgb1 & 0xFF000000 | r2 << 16 | g2 << 8 | b2;
                int er = r1 - r2;
                int eg = g1 - g2;
                int eb = b1 - b2;
                int i3 = -1;
                while (i3 <= 1) {
                    int iy = i3 + y;
                    if (iy >= 0 && iy < height) {
                        int j = -1;
                        while (j <= 1) {
                            int w;
                            int jx = j + x;
                            if (jx >= 0 && jx < width && (w = reverse ? this.matrix[(i3 + 1) * 3 - j + 1] : this.matrix[(i3 + 1) * 3 + j + 1]) != 0) {
                                int k = reverse ? index - j : index + j;
                                rgb1 = inPixels[k];
                                r1 = rgb1 >> 16 & 0xFF;
                                g1 = rgb1 >> 8 & 0xFF;
                                b1 = rgb1 & 0xFF;
                                inPixels[k] = inPixels[k] & 0xFF000000 | PixelUtils.clamp(r1 += er * w / this.sum) << 16 | PixelUtils.clamp(g1 += eg * w / this.sum) << 8 | PixelUtils.clamp(b1 += eb * w / this.sum);
                            }
                            ++j;
                        }
                    }
                    ++i3;
                }
                index += direction;
                ++x;
            }
            ++y;
        }
        return outPixels;
    }

    public String toString() {
        return "Colors/Diffusion Dither...";
    }
}
