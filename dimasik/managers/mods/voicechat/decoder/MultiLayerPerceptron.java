package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.MLPState;
import dimasik.managers.mods.voicechat.decoder.OpusTables;

class MultiLayerPerceptron {
    private static final int MAX_NEURONS = 100;

    MultiLayerPerceptron() {
    }

    static float tansig_approx(float x) {
        float sign = 1.0f;
        if (!(x < 8.0f)) {
            return 1.0f;
        }
        if (!(x > -8.0f)) {
            return -1.0f;
        }
        if (x < 0.0f) {
            x = -x;
            sign = -1.0f;
        }
        int i = (int)Math.floor(0.5f + 25.0f * x);
        float y = OpusTables.tansig_table[i];
        float dy = 1.0f - y * y;
        y += (x -= 0.04f * (float)i) * dy * (1.0f - y * x);
        return sign * y;
    }

    static void mlp_process(MLPState m, float[] input, float[] output) {
        int k;
        float sum;
        int j;
        float[] hidden = new float[100];
        float[] W = m.weights;
        int W_ptr = 0;
        for (j = 0; j < m.topo[1]; ++j) {
            sum = W[W_ptr];
            ++W_ptr;
            for (k = 0; k < m.topo[0]; ++k) {
                sum += input[k] * W[W_ptr];
                ++W_ptr;
            }
            hidden[j] = MultiLayerPerceptron.tansig_approx(sum);
        }
        for (j = 0; j < m.topo[2]; ++j) {
            sum = W[W_ptr];
            ++W_ptr;
            for (k = 0; k < m.topo[1]; ++k) {
                sum += hidden[k] * W[W_ptr];
                ++W_ptr;
            }
            output[j] = MultiLayerPerceptron.tansig_approx(sum);
        }
    }
}
