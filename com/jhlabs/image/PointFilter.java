package com.jhlabs.image;

import com.jhlabs.image.AbstractBufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public abstract class PointFilter
extends AbstractBufferedImageOp {
    protected boolean canFilterIndexColorModel = false;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        int type = src.getType();
        WritableRaster srcRaster = src.getRaster();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        WritableRaster dstRaster = dst.getRaster();
        this.setDimensions(width, height);
        int[] inPixels = new int[width];
        int y = 0;
        while (y < height) {
            int x;
            if (type == 2) {
                srcRaster.getDataElements(0, y, width, 1, inPixels);
                x = 0;
                while (x < width) {
                    inPixels[x] = this.filterRGB(x, y, inPixels[x]);
                    ++x;
                }
                dstRaster.setDataElements(0, y, width, 1, inPixels);
            } else {
                src.getRGB(0, y, width, 1, inPixels, 0, width);
                x = 0;
                while (x < width) {
                    inPixels[x] = this.filterRGB(x, y, inPixels[x]);
                    ++x;
                }
                dst.setRGB(0, y, width, 1, inPixels, 0, width);
            }
            ++y;
        }
        return dst;
    }

    public void setDimensions(int width, int height) {
    }

    public abstract int filterRGB(int var1, int var2, int var3);
}
