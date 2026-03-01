package dimasik.managers.mods.voicechat.decoder;

class Downmix {
    Downmix() {
    }

    static void downmix_int(short[] x, int x_ptr, int[] sub, int sub_ptr, int subframe, int offset, int c1, int c2, int C) {
        int j;
        for (j = 0; j < subframe; ++j) {
            sub[j + sub_ptr] = x[(j + offset) * C + c1];
        }
        if (c2 > -1) {
            for (j = 0; j < subframe; ++j) {
                int n = j + sub_ptr;
                sub[n] = sub[n] + x[(j + offset) * C + c2];
            }
        } else if (c2 == -2) {
            for (int c = 1; c < C; ++c) {
                for (j = 0; j < subframe; ++j) {
                    int n = j + sub_ptr;
                    sub[n] = sub[n] + x[(j + offset) * C + c];
                }
            }
        }
        int scale = 4096;
        scale = C == -2 ? (scale /= C) : (scale /= 2);
        for (j = 0; j < subframe; ++j) {
            int n = j + sub_ptr;
            sub[n] = sub[n] * scale;
        }
    }
}
