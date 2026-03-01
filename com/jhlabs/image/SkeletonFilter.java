package com.jhlabs.image;

import com.jhlabs.image.BinaryFilter;
import java.awt.Rectangle;

public class SkeletonFilter
extends BinaryFilter {
    private static final byte[] skeletonTable;

    static {
        byte[] byArray = new byte[256];
        byArray[3] = 1;
        byArray[6] = 1;
        byArray[7] = 3;
        byArray[10] = 3;
        byArray[11] = 1;
        byArray[12] = 1;
        byArray[14] = 1;
        byArray[15] = 3;
        byArray[24] = 2;
        byArray[26] = 2;
        byArray[28] = 3;
        byArray[30] = 3;
        byArray[31] = 3;
        byArray[40] = 3;
        byArray[48] = 2;
        byArray[56] = 2;
        byArray[60] = 3;
        byArray[62] = 2;
        byArray[63] = 2;
        byArray[96] = 2;
        byArray[104] = 2;
        byArray[108] = 2;
        byArray[112] = 3;
        byArray[120] = 3;
        byArray[124] = 3;
        byArray[126] = 2;
        byArray[129] = 1;
        byArray[130] = 3;
        byArray[131] = 1;
        byArray[134] = 1;
        byArray[135] = 3;
        byArray[143] = 1;
        byArray[159] = 1;
        byArray[160] = 3;
        byArray[161] = 1;
        byArray[176] = 2;
        byArray[192] = 2;
        byArray[193] = 3;
        byArray[194] = 1;
        byArray[195] = 3;
        byArray[198] = 1;
        byArray[199] = 3;
        byArray[207] = 1;
        byArray[224] = 2;
        byArray[225] = 3;
        byArray[227] = 1;
        byArray[231] = 1;
        byArray[240] = 3;
        byArray[241] = 3;
        byArray[243] = 1;
        byArray[248] = 2;
        byArray[249] = 2;
        byArray[252] = 2;
        skeletonTable = byArray;
    }

    public SkeletonFilter() {
        this.newColor = -1;
    }

    @Override
    protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
        int[] outPixels = new int[width * height];
        int count = 0;
        int black = -16777216;
        int white = -1;
        int i = 0;
        while (i < this.iterations) {
            count = 0;
            int pass = 0;
            while (pass < 2) {
                int y = 1;
                while (y < height - 1) {
                    int offset = y * width + 1;
                    int x = 1;
                    while (x < width - 1) {
                        int pixel = inPixels[offset];
                        if (pixel == black) {
                            int tableIndex = 0;
                            if (inPixels[offset - width - 1] == black) {
                                tableIndex |= 1;
                            }
                            if (inPixels[offset - width] == black) {
                                tableIndex |= 2;
                            }
                            if (inPixels[offset - width + 1] == black) {
                                tableIndex |= 4;
                            }
                            if (inPixels[offset + 1] == black) {
                                tableIndex |= 8;
                            }
                            if (inPixels[offset + width + 1] == black) {
                                tableIndex |= 0x10;
                            }
                            if (inPixels[offset + width] == black) {
                                tableIndex |= 0x20;
                            }
                            if (inPixels[offset + width - 1] == black) {
                                tableIndex |= 0x40;
                            }
                            if (inPixels[offset - 1] == black) {
                                tableIndex |= 0x80;
                            }
                            byte code = skeletonTable[tableIndex];
                            if (pass == 1) {
                                if (code == 2 || code == 3) {
                                    pixel = this.colormap != null ? this.colormap.getColor((float)i / (float)this.iterations) : this.newColor;
                                    ++count;
                                }
                            } else if (code == 1 || code == 3) {
                                pixel = this.colormap != null ? this.colormap.getColor((float)i / (float)this.iterations) : this.newColor;
                                ++count;
                            }
                        }
                        outPixels[offset++] = pixel;
                        ++x;
                    }
                    ++y;
                }
                if (pass == 0) {
                    inPixels = outPixels;
                    outPixels = new int[width * height];
                }
                ++pass;
            }
            if (count == 0) break;
            ++i;
        }
        return outPixels;
    }

    public String toString() {
        return "Binary/Skeletonize...";
    }
}
