package com.jhlabs.image;

import com.jhlabs.image.BinaryFilter;
import java.awt.Rectangle;

public class LifeFilter
extends BinaryFilter {
    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        int[] outPixels = new int[width * height];
        int y = 0;
        while (y < height) {
            int x = 0;
            while (x < width) {
                boolean r = false;
                boolean g = false;
                boolean b = false;
                int pixel = inPixels[y * width + x];
                int a = pixel & 0xFF000000;
                int neighbours = 0;
                int row = -1;
                while (row <= 1) {
                    int iy = y + row;
                    if (iy >= 0 && iy < height) {
                        int ioffset = iy * width;
                        int col = -1;
                        while (col <= 1) {
                            int rgb;
                            int ix = x + col;
                            if ((row != 0 || col != 0) && ix >= 0 && ix < width && this.blackFunction.isBlack(rgb = inPixels[ioffset + ix])) {
                                ++neighbours;
                            }
                            ++col;
                        }
                    }
                    ++row;
                }
                if (this.blackFunction.isBlack(pixel)) {
                    outPixels[index++] = neighbours == 2 || neighbours == 3 ? pixel : -1;
                } else {
                    outPixels[index++] = neighbours == 3 ? -16777216 : pixel;
                }
                ++x;
            }
            ++y;
        }
        return outPixels;
    }

    public String toString() {
        return "Binary/Life";
    }
}
