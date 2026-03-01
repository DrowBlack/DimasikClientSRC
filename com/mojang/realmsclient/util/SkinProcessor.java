package com.mojang.realmsclient.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.annotation.Nullable;

public class SkinProcessor {
    private int[] field_225230_a;
    private int field_225231_b;
    private int field_225232_c;

    @Nullable
    public BufferedImage func_225228_a(BufferedImage p_225228_1_) {
        boolean flag;
        if (p_225228_1_ == null) {
            return null;
        }
        this.field_225231_b = 64;
        this.field_225232_c = 64;
        BufferedImage bufferedimage = new BufferedImage(this.field_225231_b, this.field_225232_c, 2);
        Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(p_225228_1_, 0, 0, null);
        boolean bl = flag = p_225228_1_.getHeight() == 32;
        if (flag) {
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 32, 64, 32);
            graphics.drawImage(bufferedimage, 24, 48, 20, 52, 4, 16, 8, 20, null);
            graphics.drawImage(bufferedimage, 28, 48, 24, 52, 8, 16, 12, 20, null);
            graphics.drawImage(bufferedimage, 20, 52, 16, 64, 8, 20, 12, 32, null);
            graphics.drawImage(bufferedimage, 24, 52, 20, 64, 4, 20, 8, 32, null);
            graphics.drawImage(bufferedimage, 28, 52, 24, 64, 0, 20, 4, 32, null);
            graphics.drawImage(bufferedimage, 32, 52, 28, 64, 12, 20, 16, 32, null);
            graphics.drawImage(bufferedimage, 40, 48, 36, 52, 44, 16, 48, 20, null);
            graphics.drawImage(bufferedimage, 44, 48, 40, 52, 48, 16, 52, 20, null);
            graphics.drawImage(bufferedimage, 36, 52, 32, 64, 48, 20, 52, 32, null);
            graphics.drawImage(bufferedimage, 40, 52, 36, 64, 44, 20, 48, 32, null);
            graphics.drawImage(bufferedimage, 44, 52, 40, 64, 40, 20, 44, 32, null);
            graphics.drawImage(bufferedimage, 48, 52, 44, 64, 52, 20, 56, 32, null);
        }
        graphics.dispose();
        this.field_225230_a = ((DataBufferInt)bufferedimage.getRaster().getDataBuffer()).getData();
        this.func_225229_b(0, 0, 32, 16);
        if (flag) {
            this.func_225227_a(32, 0, 64, 32);
        }
        this.func_225229_b(0, 16, 64, 32);
        this.func_225229_b(16, 48, 48, 64);
        return bufferedimage;
    }

    private void func_225227_a(int p_225227_1_, int p_225227_2_, int p_225227_3_, int p_225227_4_) {
        for (int i = p_225227_1_; i < p_225227_3_; ++i) {
            for (int j = p_225227_2_; j < p_225227_4_; ++j) {
                int k = this.field_225230_a[i + j * this.field_225231_b];
                if ((k >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int l = p_225227_1_; l < p_225227_3_; ++l) {
            for (int i1 = p_225227_2_; i1 < p_225227_4_; ++i1) {
                int n = l + i1 * this.field_225231_b;
                this.field_225230_a[n] = this.field_225230_a[n] & 0xFFFFFF;
            }
        }
    }

    private void func_225229_b(int p_225229_1_, int p_225229_2_, int p_225229_3_, int p_225229_4_) {
        for (int i = p_225229_1_; i < p_225229_3_; ++i) {
            for (int j = p_225229_2_; j < p_225229_4_; ++j) {
                int n = i + j * this.field_225231_b;
                this.field_225230_a[n] = this.field_225230_a[n] | 0xFF000000;
            }
        }
    }
}
