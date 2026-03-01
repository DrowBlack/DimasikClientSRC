package com.jhlabs.image;

import com.jhlabs.image.BinaryFilter;
import java.awt.Rectangle;

public class DilateFilter
extends BinaryFilter {
    private int threshold = 2;

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return this.threshold;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int[] outPixels = new int[width * height];
        int i = 0;
        while (i < this.iterations) {
            int index = 0;
            if (i > 0) {
                int[] t = inPixels;
                inPixels = outPixels;
                outPixels = t;
            }
            int y = 0;
            while (y < height) {
                int x = 0;
                while (x < width) {
                    int pixel = inPixels[y * width + x];
                    if (!this.blackFunction.isBlack(pixel)) {
                        int neighbours = 0;
                        int dy = -1;
                        while (dy <= 1) {
                            int iy = y + dy;
                            if (iy >= 0 && iy < height) {
                                int ioffset = iy * width;
                                int dx = -1;
                                while (dx <= 1) {
                                    int rgb;
                                    int ix = x + dx;
                                    if ((dy != 0 || dx != 0) && ix >= 0 && ix < width && this.blackFunction.isBlack(rgb = inPixels[ioffset + ix])) {
                                        ++neighbours;
                                    }
                                    ++dx;
                                }
                            }
                            ++dy;
                        }
                        if (neighbours >= this.threshold) {
                            pixel = this.colormap != null ? this.colormap.getColor((float)i / (float)this.iterations) : this.newColor;
                        }
                    }
                    outPixels[index++] = pixel;
                    ++x;
                }
                ++y;
            }
            ++i;
        }
        return outPixels;
    }

    public String toString() {
        return "Binary/Dilate...";
    }
}
