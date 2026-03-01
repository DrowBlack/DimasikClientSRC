package dimasik.utils.math;

public class CustRandom {
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = -1727483681;
    private static final int UPPER_MASK = Integer.MIN_VALUE;
    private static final int LOWER_MASK = Integer.MAX_VALUE;
    private int[] mt = new int[624];
    private int index = 625;
    private long lastGenerationTime = 0L;
    private long delay = 0L;
    private float lastGeneratedNumber = 0.0f;

    public CustRandom(long seed) {
        this.init(seed);
    }

    public CustRandom() {
        this(System.currentTimeMillis());
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return this.delay;
    }

    private void init(long seed) {
        this.mt[0] = (int)(seed & 0xFFFFFFFFL);
        this.index = 1;
        while (this.index < 624) {
            this.mt[this.index] = 1812433253 * (this.mt[this.index - 1] ^ this.mt[this.index - 1] >>> 30) + this.index;
            int n = this.index++;
            this.mt[n] = this.mt[n] & 0xFFFFFFFF;
        }
    }

    private int nextInt() {
        if (this.index >= 624) {
            this.twist();
        }
        int y = this.mt[this.index++];
        y ^= y >>> 11;
        y ^= y << 7 & 0x9D2C5680;
        y ^= y << 15 & 0xEFC60000;
        y ^= y >>> 18;
        return y;
    }

    public float nextFloat() {
        return (float)((long)this.nextInt() & 0xFFFFFFFFL) / 4.2949673E9f;
    }

    public float randomNumber(float firstNumber, float secondNumber, boolean isInt) {
        float result;
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastGenerationTime < this.delay) {
            return this.lastGeneratedNumber;
        }
        if (firstNumber > secondNumber) {
            float temp = firstNumber;
            firstNumber = secondNumber;
            secondNumber = temp;
        }
        do {
            float random = this.nextFloat();
            result = firstNumber + random * (secondNumber - firstNumber);
            if (!isInt) continue;
            result = (int)result;
        } while (result == this.lastGeneratedNumber);
        this.lastGenerationTime = currentTime;
        this.lastGeneratedNumber = result;
        return result;
    }

    private void twist() {
        for (int i = 0; i < 624; ++i) {
            int x = (this.mt[i] & Integer.MIN_VALUE) + (this.mt[(i + 1) % 624] & Integer.MAX_VALUE);
            int xA = x >>> 1;
            if (x % 2 != 0) {
                xA ^= 0x9908B0DF;
            }
            this.mt[i] = this.mt[(i + 397) % 624] ^ xA;
        }
        this.index = 0;
    }
}
