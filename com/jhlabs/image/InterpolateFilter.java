package com.jhlabs.image;

import com.jhlabs.image.AbstractBufferedImageOp;
import com.jhlabs.image.ImageMath;
import com.jhlabs.image.PixelUtils;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class InterpolateFilter
extends AbstractBufferedImageOp {
    private BufferedImage destination;
    private float interpolation;

    public void setDestination(BufferedImage destination) {
        this.destination = destination;
    }

    public BufferedImage getDestination() {
        return this.destination;
    }

    public void setInterpolation(float interpolation) {
        this.interpolation = interpolation;
    }

    public float getInterpolation() {
        return this.interpolation;
    }

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
        if (this.destination != null) {
            width = Math.min(width, this.destination.getWidth());
            height = Math.min(height, this.destination.getWidth());
            int[] pixels1 = null;
            int[] pixels2 = null;
            int y = 0;
            while (y < height) {
                pixels1 = this.getRGB(src, 0, y, width, 1, pixels1);
                pixels2 = this.getRGB(this.destination, 0, y, width, 1, pixels2);
                int x = 0;
                while (x < width) {
                    int rgb1 = pixels1[x];
                    int rgb2 = pixels2[x];
                    int a1 = rgb1 >> 24 & 0xFF;
                    int r1 = rgb1 >> 16 & 0xFF;
                    int g1 = rgb1 >> 8 & 0xFF;
                    int b1 = rgb1 & 0xFF;
                    int a2 = rgb2 >> 24 & 0xFF;
                    int r2 = rgb2 >> 16 & 0xFF;
                    int g2 = rgb2 >> 8 & 0xFF;
                    int b2 = rgb2 & 0xFF;
                    r1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, r1, r2));
                    g1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, g1, g2));
                    b1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, b1, b2));
                    pixels1[x] = a1 << 24 | r1 << 16 | g1 << 8 | b1;
                    ++x;
                }
                this.setRGB(dst, 0, y, width, 1, pixels1);
                ++y;
            }
        }
        return dst;
    }

    public String toString() {
        return "Effects/Interpolate...";
    }
}
