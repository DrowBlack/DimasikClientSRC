package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;

class Sort {
    Sort() {
    }

    static void silk_insertion_sort_increasing(int[] a, int[] idx, int L, int K) {
        int j;
        int value;
        int i;
        Inlines.OpusAssert(K > 0);
        Inlines.OpusAssert(L > 0);
        Inlines.OpusAssert(L >= K);
        for (i = 0; i < K; ++i) {
            idx[i] = i;
        }
        i = 1;
        while (i < K) {
            value = a[i];
            for (j = i - 1; j >= 0 && value < a[j]; --j) {
                a[j + 1] = a[j];
                idx[j + 1] = idx[j];
            }
            a[j + 1] = value;
            idx[j + 1] = i++;
        }
        for (i = K; i < L; ++i) {
            value = a[i];
            if (value >= a[K - 1]) continue;
            for (j = K - 2; j >= 0 && value < a[j]; --j) {
                a[j + 1] = a[j];
                idx[j + 1] = idx[j];
            }
            a[j + 1] = value;
            idx[j + 1] = i;
        }
    }

    static void silk_insertion_sort_increasing_all_values_int16(short[] a, int L) {
        Inlines.OpusAssert(L > 0);
        for (int i = 1; i < L; ++i) {
            short value = a[i];
            for (int j = i - 1; j >= 0 && value < a[j]; --j) {
                a[j + 1] = a[j];
            }
            a[j + 1] = value;
        }
    }

    static void silk_insertion_sort_decreasing_int16(short[] a, int[] idx, int L, int K) {
        int j;
        short value;
        int i;
        Inlines.OpusAssert(K > 0);
        Inlines.OpusAssert(L > 0);
        Inlines.OpusAssert(L >= K);
        for (i = 0; i < K; ++i) {
            idx[i] = i;
        }
        i = 1;
        while (i < K) {
            value = a[i];
            for (j = i - 1; j >= 0 && value > a[j]; --j) {
                a[j + 1] = a[j];
                idx[j + 1] = idx[j];
            }
            a[j + 1] = value;
            idx[j + 1] = i++;
        }
        for (i = K; i < L; ++i) {
            value = a[i];
            if (value <= a[K - 1]) continue;
            for (j = K - 2; j >= 0 && value > a[j]; --j) {
                a[j + 1] = a[j];
                idx[j + 1] = idx[j];
            }
            a[j + 1] = value;
            idx[j + 1] = i;
        }
    }
}
