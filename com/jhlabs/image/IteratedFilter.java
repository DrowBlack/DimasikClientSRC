package com.jhlabs.image;

import com.jhlabs.image.AbstractBufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class IteratedFilter
extends AbstractBufferedImageOp {
    private BufferedImageOp filter;
    private int iterations;

    public IteratedFilter(BufferedImageOp filter, int iterations) {
        this.filter = filter;
        this.iterations = iterations;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        BufferedImage image = src;
        int i = 0;
        while (i < this.iterations) {
            image = this.filter.filter(image, dst);
            ++i;
        }
        return image;
    }
}
