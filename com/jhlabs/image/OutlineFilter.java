package com.jhlabs.image;

import com.jhlabs.image.BinaryFilter;
import java.awt.Rectangle;

public class OutlineFilter
extends BinaryFilter {
    public OutlineFilter() {
        this.newColor = -1;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        int[] outPixels = new int[width * height];
        int y = 0;
        while (y < height) {
            int x = 0;
            while (x < width) {
                int pixel = inPixels[y * width + x];
                if (this.blackFunction.isBlack(pixel)) {
                    int neighbours = 0;
                    int dy = -1;
                    while (dy <= 1) {
                        int iy = y + dy;
                        if (iy >= 0 && iy < height) {
                            int ioffset = iy * width;
                            int dx = -1;
                            while (dx <= 1) {
                                int ix = x + dx;
                                if ((dy != 0 || dx != 0) && ix >= 0 && ix < width) {
                                    int rgb = inPixels[ioffset + ix];
                                    if (this.blackFunction.isBlack(rgb)) {
                                        ++neighbours;
                                    }
                                } else {
                                    ++neighbours;
                                }
                                ++dx;
                            }
                        }
                        ++dy;
                    }
                    if (neighbours == 9) {
                        pixel = this.newColor;
                    }
                }
                outPixels[index++] = pixel;
                ++x;
            }
            ++y;
        }
        return outPixels;
    }

    public String toString() {
        return "Binary/Outline...";
    }
}
