package com.jhlabs.image;

import com.jhlabs.image.PixelUtils;
import com.jhlabs.image.WholeImageFilter;
import java.awt.Rectangle;

public class ContourFilter
extends WholeImageFilter {
    private float levels = 5.0f;
    private float scale = 1.0f;
    private float offset = 0.0f;
    private int contourColor = -16777216;

    public void setLevels(float levels) {
        this.levels = levels;
    }

    public float getLevels() {
        return this.levels;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return this.offset;
    }

    public void setContourColor(int contourColor) {
        this.contourColor = contourColor;
    }

    public int getContourColor() {
        return this.contourColor;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        short[][] r = new short[3][width];
        int[] outPixels = new int[width * height];
        short[] table = new short[256];
        int offsetl = (int)(this.offset * 256.0f / this.levels);
        int i = 0;
        while (i < 256) {
            table[i] = (short)PixelUtils.clamp((int)(255.0 * Math.floor(this.levels * (float)(i + offsetl) / 256.0f) / (double)(this.levels - 1.0f) - (double)offsetl));
            ++i;
        }
        int x = 0;
        while (x < width) {
            int rgb = inPixels[x];
            r[1][x] = (short)PixelUtils.brightness(rgb);
            ++x;
        }
        int y = 0;
        while (y < height) {
            int x2;
            boolean yIn = y > 0 && y < height - 1;
            int nextRowIndex = index + width;
            if (y < height - 1) {
                x2 = 0;
                while (x2 < width) {
                    int rgb = inPixels[nextRowIndex++];
                    r[2][x2] = (short)PixelUtils.brightness(rgb);
                    ++x2;
                }
            }
            x2 = 0;
            while (x2 < width) {
                boolean xIn = x2 > 0 && x2 < width - 1;
                int w = x2 - 1;
                int e = x2 + 1;
                int v = 0;
                if (yIn && xIn) {
                    short nwb = r[0][w];
                    short neb = r[0][x2];
                    short swb = r[1][w];
                    short seb = r[1][x2];
                    short nw = table[nwb];
                    short ne = table[neb];
                    short sw = table[swb];
                    short se = table[seb];
                    if ((nw != ne || nw != sw || ne != se || sw != se) && (v = (int)(this.scale * (float)(Math.abs(nwb - neb) + Math.abs(nwb - swb) + Math.abs(neb - seb) + Math.abs(swb - seb)))) > 255) {
                        v = 255;
                    }
                }
                outPixels[index] = v != 0 ? PixelUtils.combinePixels(inPixels[index], this.contourColor, 1, v) : inPixels[index];
                ++index;
                ++x2;
            }
            short[] t = r[0];
            r[0] = r[1];
            r[1] = r[2];
            r[2] = t;
            ++y;
        }
        return outPixels;
    }

    public String toString() {
        return "Stylize/Contour...";
    }
}
