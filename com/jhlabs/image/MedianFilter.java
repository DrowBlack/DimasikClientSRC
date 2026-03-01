package com.jhlabs.image;

import com.jhlabs.image.WholeImageFilter;
import java.awt.Rectangle;

public class MedianFilter
extends WholeImageFilter {
    private int median(int[] array) {
        int max;
        int i = 0;
        while (i < 4) {
            max = 0;
            int maxIndex = 0;
            int j = 0;
            while (j < 9) {
                if (array[j] > max) {
                    max = array[j];
                    maxIndex = j;
                }
                ++j;
            }
            array[maxIndex] = 0;
            ++i;
        }
        max = 0;
        i = 0;
        while (i < 9) {
            if (array[i] > max) {
                max = array[i];
            }
            ++i;
        }
        return max;
    }

    private int rgbMedian(int[] r, int[] g, int[] b) {
        int index = 0;
        int min = Integer.MAX_VALUE;
        int i = 0;
        while (i < 9) {
            int sum = 0;
            int j = 0;
            while (j < 9) {
                sum += Math.abs(r[i] - r[j]);
                sum += Math.abs(g[i] - g[j]);
                sum += Math.abs(b[i] - b[j]);
                ++j;
            }
            if (sum < min) {
                min = sum;
                index = i;
            }
            ++i;
        }
        return index;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int index = 0;
        int[] argb = new int[9];
        int[] r = new int[9];
        int[] g = new int[9];
        int[] b = new int[9];
        int[] outPixels = new int[width * height];
        int y = 0;
        while (y < height) {
            int x = 0;
            while (x < width) {
                int k = 0;
                int dy = -1;
                while (dy <= 1) {
                    int iy = y + dy;
                    if (iy >= 0 && iy < height) {
                        int ioffset = iy * width;
                        int dx = -1;
                        while (dx <= 1) {
                            int ix = x + dx;
                            if (ix >= 0 && ix < width) {
                                int rgb;
                                argb[k] = rgb = inPixels[ioffset + ix];
                                r[k] = rgb >> 16 & 0xFF;
                                g[k] = rgb >> 8 & 0xFF;
                                b[k] = rgb & 0xFF;
                                ++k;
                            }
                            ++dx;
                        }
                    }
                    ++dy;
                }
                while (k < 9) {
                    argb[k] = -16777216;
                    b[k] = 0;
                    g[k] = 0;
                    r[k] = 0;
                    ++k;
                }
                outPixels[index++] = argb[this.rgbMedian(r, g, b)];
                ++x;
            }
            ++y;
        }
        return outPixels;
    }

    public String toString() {
        return "Blur/Median";
    }
}
