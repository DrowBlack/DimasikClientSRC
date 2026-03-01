package dimasik.utils.shader.main;

public class KernelBlur {
    private final int size;
    private final float[] kernel;

    public KernelBlur(int size) {
        this.size = size;
        this.kernel = new float[size];
    }

    public void compute() {
        int i;
        float sigma = (float)this.size / 2.0f;
        float kernelSum = 0.0f;
        for (i = 0; i < this.size; ++i) {
            float multiplier = (float)i / sigma;
            this.kernel[i] = 1.0f / (Math.abs(sigma) * 2.5066283f) * (float)Math.exp(-0.5 * (double)multiplier * (double)multiplier);
            kernelSum += i > 0 ? this.kernel[i] * 2.0f : this.kernel[0];
        }
        i = 0;
        while (i < this.size) {
            int n = i++;
            this.kernel[n] = this.kernel[n] / kernelSum;
        }
    }

    public int getSize() {
        return this.size;
    }

    public float[] getKernel() {
        return this.kernel;
    }
}
