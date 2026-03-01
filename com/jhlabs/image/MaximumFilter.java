package com.jhlabs.image;

import com.jhlabs.image.PixelUtils;
import com.jhlabs.image.WholeImageFilter;
import java.awt.Rectangle;

public class MaximumFilter
extends WholeImageFilter {
    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        int[] outPixels = new int[width * height];
        int y = 0;
        while (y < height) {
            int x = 0;
            while (x < width) {
                int pixel = -16777216;
                int dy = -1;
                while (dy <= 1) {
                    int iy = y + dy;
                    if (iy >= 0 && iy < height) {
                        int ioffset = iy * width;
                        int dx = -1;
                        while (dx <= 1) {
                            int ix = x + dx;
                            if (ix >= 0 && ix < width) {
                                pixel = PixelUtils.combinePixels(pixel, inPixels[ioffset + ix], 3);
                            }
                            ++dx;
                        }
                    }
                    ++dy;
                }
                outPixels[index++] = pixel;
                ++x;
            }
            ++y;
        }
        return outPixels;
    }

    public String toString() {
        return "Blur/Maximum";
    }
}
