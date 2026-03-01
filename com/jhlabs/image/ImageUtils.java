package com.jhlabs.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public abstract class ImageUtils {
    private static BufferedImage backgroundImage = null;

    public static BufferedImage createImage(ImageProducer producer) {
        PixelGrabber pg = new PixelGrabber(producer, 0, 0, -1, -1, null, 0, 0);
        try {
            pg.grabPixels();
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Image fetch interrupted");
        }
        if ((pg.status() & 0x80) != 0) {
            throw new RuntimeException("Image fetch aborted");
        }
        if ((pg.status() & 0x40) != 0) {
            throw new RuntimeException("Image fetch error");
        }
        BufferedImage p = new BufferedImage(pg.getWidth(), pg.getHeight(), 2);
        p.setRGB(0, 0, pg.getWidth(), pg.getHeight(), (int[])pg.getPixels(), 0, pg.getWidth());
        return p;
    }

    public static BufferedImage convertImageToARGB(Image image) {
        if (image instanceof BufferedImage && ((BufferedImage)image).getType() == 2) {
            return (BufferedImage)image;
        }
        BufferedImage p = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        Graphics2D g = p.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return p;
    }

    public static BufferedImage getSubimage(BufferedImage image, int x, int y, int w, int h) {
        BufferedImage newImage = new BufferedImage(w, h, 2);
        Graphics2D g = newImage.createGraphics();
        g.drawRenderedImage(image, AffineTransform.getTranslateInstance(-x, -y));
        g.dispose();
        return newImage;
    }

    public static BufferedImage cloneImage(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
        Graphics2D g = newImage.createGraphics();
        g.drawRenderedImage(image, null);
        g.dispose();
        return newImage;
    }

    public static void paintCheckedBackground(Component c, Graphics g, int x, int y, int width, int height) {
        if (backgroundImage == null) {
            backgroundImage = new BufferedImage(64, 64, 2);
            Graphics2D bg = backgroundImage.createGraphics();
            int by = 0;
            while (by < 64) {
                int bx = 0;
                while (bx < 64) {
                    bg.setColor(((bx ^ by) & 8) != 0 ? Color.lightGray : Color.white);
                    bg.fillRect(bx, by, 8, 8);
                    bx += 8;
                }
                by += 8;
            }
            bg.dispose();
        }
        if (backgroundImage != null) {
            Shape saveClip = g.getClip();
            Rectangle r = g.getClipBounds();
            if (r == null) {
                r = new Rectangle(c.getSize());
            }
            r = r.intersection(new Rectangle(x, y, width, height));
            g.setClip(r);
            int w = backgroundImage.getWidth();
            int h = backgroundImage.getHeight();
            if (w != -1 && h != -1) {
                int x1 = r.x / w * w;
                int y1 = r.y / h * h;
                int x2 = (r.x + r.width + w - 1) / w * w;
                int y2 = (r.y + r.height + h - 1) / h * h;
                y = y1;
                while (y < y2) {
                    x = x1;
                    while (x < x2) {
                        g.drawImage(backgroundImage, x, y, c);
                        x += w;
                    }
                    y += h;
                }
            }
            g.setClip(saveClip);
        }
    }

    public static Rectangle getSelectedBounds(BufferedImage p) {
        int width = p.getWidth();
        int height = p.getHeight();
        int maxX = 0;
        int maxY = 0;
        int minX = width;
        int minY = height;
        boolean anySelected = false;
        int[] pixels = null;
        int y1 = height - 1;
        while (y1 >= 0) {
            pixels = ImageUtils.getRGB(p, 0, y1, width, 1, pixels);
            int x = 0;
            while (x < minX) {
                if ((pixels[x] & 0xFF000000) != 0) {
                    minX = x;
                    maxY = y1;
                    anySelected = true;
                    break;
                }
                ++x;
            }
            x = width - 1;
            while (x >= maxX) {
                if ((pixels[x] & 0xFF000000) != 0) {
                    maxX = x;
                    maxY = y1;
                    anySelected = true;
                    break;
                }
                --x;
            }
            if (anySelected) break;
            --y1;
        }
        pixels = null;
        int y = 0;
        while (y < y1) {
            pixels = ImageUtils.getRGB(p, 0, y, width, 1, pixels);
            int x = 0;
            while (x < minX) {
                if ((pixels[x] & 0xFF000000) != 0) {
                    minX = x;
                    if (y < minY) {
                        minY = y;
                    }
                    anySelected = true;
                    break;
                }
                ++x;
            }
            x = width - 1;
            while (x >= maxX) {
                if ((pixels[x] & 0xFF000000) != 0) {
                    maxX = x;
                    if (y < minY) {
                        minY = y;
                    }
                    anySelected = true;
                    break;
                }
                --x;
            }
            ++y;
        }
        if (anySelected) {
            return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
        return null;
    }

    public static void composeThroughMask(Raster src, WritableRaster dst, Raster sel) {
        int x = src.getMinX();
        int y = src.getMinY();
        int w = src.getWidth();
        int h = src.getHeight();
        int[] srcRGB = null;
        int[] selRGB = null;
        int[] dstRGB = null;
        int i = 0;
        while (i < h) {
            srcRGB = src.getPixels(x, y, w, 1, srcRGB);
            selRGB = sel.getPixels(x, y, w, 1, selRGB);
            dstRGB = dst.getPixels(x, y, w, 1, dstRGB);
            int k = x;
            int j = 0;
            while (j < w) {
                int sr = srcRGB[k];
                int dir = dstRGB[k];
                int sg = srcRGB[k + 1];
                int dig = dstRGB[k + 1];
                int sb = srcRGB[k + 2];
                int dib = dstRGB[k + 2];
                int sa = srcRGB[k + 3];
                int dia = dstRGB[k + 3];
                float a = (float)selRGB[k + 3] / 255.0f;
                float ac = 1.0f - a;
                dstRGB[k] = (int)(a * (float)sr + ac * (float)dir);
                dstRGB[k + 1] = (int)(a * (float)sg + ac * (float)dig);
                dstRGB[k + 2] = (int)(a * (float)sb + ac * (float)dib);
                dstRGB[k + 3] = (int)(a * (float)sa + ac * (float)dia);
                k += 4;
                ++j;
            }
            dst.setPixels(x, y, w, 1, dstRGB);
            ++y;
            ++i;
        }
    }

    public static int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == 2 || type == 1) {
            return (int[])image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }

    public static void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == 2 || type == 1) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        } else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }
}
