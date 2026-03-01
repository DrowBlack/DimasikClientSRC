package com.jhlabs.image;

import com.jhlabs.image.WholeImageFilter;
import java.awt.Rectangle;

public class ReduceNoiseFilter
extends WholeImageFilter {
    private int smooth(int[] v) {
        int minindex = 0;
        int maxindex = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int i = 0;
        while (i < 9) {
            if (i != 4) {
                if (v[i] < min) {
                    min = v[i];
                    minindex = i;
                }
                if (v[i] > max) {
                    max = v[i];
                    maxindex = i;
                }
            }
            ++i;
        }
        if (v[4] < min) {
            return v[minindex];
        }
        if (v[4] > max) {
            return v[maxindex];
        }
        return v[4];
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        int[] r = new int[9];
        int[] g = new int[9];
        int[] b = new int[9];
        int[] outPixels = new int[width * height];
        int y = 0;
        while (y < height) {
            int x = 0;
            while (x < width) {
                int k = 0;
                int irgb = inPixels[index];
                int ir = irgb >> 16 & 0xFF;
                int ig = irgb >> 8 & 0xFF;
                int ib = irgb & 0xFF;
                int dy = -1;
                while (dy <= 1) {
                    int iy = y + dy;
                    if (iy >= 0 && iy < height) {
                        int ioffset = iy * width;
                        int dx = -1;
                        while (dx <= 1) {
                            int ix = x + dx;
                            if (ix >= 0 && ix < width) {
                                int rgb = inPixels[ioffset + ix];
                                r[k] = rgb >> 16 & 0xFF;
                                g[k] = rgb >> 8 & 0xFF;
                                b[k] = rgb & 0xFF;
                            } else {
                                r[k] = ir;
                                g[k] = ig;
                                b[k] = ib;
                            }
                            ++k;
                            ++dx;
                        }
                    } else {
                        int dx = -1;
                        while (dx <= 1) {
                            r[k] = ir;
                            g[k] = ig;
                            b[k] = ib;
                            ++k;
                            ++dx;
                        }
                    }
                    ++dy;
                }
                outPixels[index] = inPixels[index] & 0xFF000000 | this.smooth(r) << 16 | this.smooth(g) << 8 | this.smooth(b);
                ++index;
                ++x;
            }
            ++y;
        }
        return outPixels;
    }

    public String toString() {
        return "Blur/Smooth";
    }
}
