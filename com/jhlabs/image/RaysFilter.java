package com.jhlabs.image;

import com.jhlabs.composite.MiscComposite;
import com.jhlabs.image.Colormap;
import com.jhlabs.image.MotionBlurOp;
import com.jhlabs.image.PixelUtils;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class RaysFilter
extends MotionBlurOp {
    private float opacity = 1.0f;
    private float threshold = 0.0f;
    private float strength = 0.5f;
    private boolean raysOnly = false;
    private Colormap colormap;

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public float getThreshold() {
        return this.threshold;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public float getStrength() {
        return this.strength;
    }

    public void setRaysOnly(boolean raysOnly) {
        this.raysOnly = raysOnly;
    }

    public boolean getRaysOnly() {
        return this.raysOnly;
    }

    public void setColormap(Colormap colormap) {
        this.colormap = colormap;
    }

    public Colormap getColormap() {
        return this.colormap;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int l;
        int b;
        int g;
        int r;
        int a;
        int rgb;
        int x;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width];
        int[] srcPixels = new int[width];
        BufferedImage rays = new BufferedImage(width, height, 2);
        int threshold3 = (int)(this.threshold * 3.0f * 255.0f);
        int y = 0;
        while (y < height) {
            this.getRGB(src, 0, y, width, 1, pixels);
            x = 0;
            while (x < width) {
                rgb = pixels[x];
                a = rgb & 0xFF000000;
                r = rgb >> 16 & 0xFF;
                g = rgb >> 8 & 0xFF;
                b = rgb & 0xFF;
                l = r + g + b;
                pixels[x] = l < threshold3 ? -16777216 : a | (l /= 3) << 16 | l << 8 | l;
                ++x;
            }
            this.setRGB(rays, 0, y, width, 1, pixels);
            ++y;
        }
        rays = super.filter(rays, null);
        y = 0;
        while (y < height) {
            this.getRGB(rays, 0, y, width, 1, pixels);
            this.getRGB(src, 0, y, width, 1, srcPixels);
            x = 0;
            while (x < width) {
                rgb = pixels[x];
                a = rgb & 0xFF000000;
                r = rgb >> 16 & 0xFF;
                g = rgb >> 8 & 0xFF;
                b = rgb & 0xFF;
                if (this.colormap != null) {
                    l = r + g + b;
                    rgb = this.colormap.getColor((float)l * this.strength * 0.33333334f);
                } else {
                    r = PixelUtils.clamp((int)((float)r * this.strength));
                    g = PixelUtils.clamp((int)((float)g * this.strength));
                    b = PixelUtils.clamp((int)((float)b * this.strength));
                    rgb = a | r << 16 | g << 8 | b;
                }
                pixels[x] = rgb;
                ++x;
            }
            this.setRGB(rays, 0, y, width, 1, pixels);
            ++y;
        }
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        Graphics2D g2 = dst.createGraphics();
        if (!this.raysOnly) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.drawRenderedImage(src, null);
        }
        g2.setComposite(MiscComposite.getInstance(1, this.opacity));
        g2.drawRenderedImage(rays, null);
        g2.dispose();
        return dst;
    }

    @Override
    public String toString() {
        return "Stylize/Rays...";
    }
}
