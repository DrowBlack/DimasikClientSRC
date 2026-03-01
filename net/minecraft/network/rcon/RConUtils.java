package net.minecraft.network.rcon;

import java.nio.charset.StandardCharsets;

public class RConUtils {
    public static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getBytesAsString(byte[] p_72661_0_, int p_72661_1_, int p_72661_2_) {
        int j;
        int i = p_72661_2_ - 1;
        int n = j = p_72661_1_ > i ? i : p_72661_1_;
        while (0 != p_72661_0_[j] && j < i) {
            ++j;
        }
        return new String(p_72661_0_, p_72661_1_, j - p_72661_1_, StandardCharsets.UTF_8);
    }

    public static int getRemainingBytesAsLEInt(byte[] p_72662_0_, int p_72662_1_) {
        return RConUtils.getBytesAsLEInt(p_72662_0_, p_72662_1_, p_72662_0_.length);
    }

    public static int getBytesAsLEInt(byte[] p_72665_0_, int p_72665_1_, int p_72665_2_) {
        return 0 > p_72665_2_ - p_72665_1_ - 4 ? 0 : p_72665_0_[p_72665_1_ + 3] << 24 | (p_72665_0_[p_72665_1_ + 2] & 0xFF) << 16 | (p_72665_0_[p_72665_1_ + 1] & 0xFF) << 8 | p_72665_0_[p_72665_1_] & 0xFF;
    }

    public static int getBytesAsBEint(byte[] p_72664_0_, int p_72664_1_, int p_72664_2_) {
        return 0 > p_72664_2_ - p_72664_1_ - 4 ? 0 : p_72664_0_[p_72664_1_] << 24 | (p_72664_0_[p_72664_1_ + 1] & 0xFF) << 16 | (p_72664_0_[p_72664_1_ + 2] & 0xFF) << 8 | p_72664_0_[p_72664_1_ + 3] & 0xFF;
    }

    public static String getByteAsHexString(byte input) {
        return "" + HEX_DIGITS[(input & 0xF0) >>> 4] + HEX_DIGITS[input & 0xF];
    }
}
