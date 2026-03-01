package dimasik.utils.math;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import lombok.Generated;

public class RandomNumberUtils {
    private final Set<Float> previousNumbers = new HashSet<Float>();
    private final float minDifference;
    private final float startRange;
    private final float endRange;
    private float current = 1.0f;
    private final int maxCapacity;

    public RandomNumberUtils(float start, float end, float minDifference) {
        this.minDifference = minDifference;
        this.startRange = start;
        this.endRange = end;
        this.maxCapacity = (int)((end - start) / minDifference);
    }

    public void generate() {
        float num;
        Random random = new Random();
        if (this.previousNumbers.size() >= this.maxCapacity) {
            this.previousNumbers.clear();
        }
        while (!this.isNumberValid(num = this.startRange + random.nextFloat() * (this.endRange - this.startRange))) {
        }
        for (float i = num - this.minDifference; i <= num + this.minDifference; i += 0.01f) {
            this.previousNumbers.add(Float.valueOf(i));
        }
        this.current = num;
    }

    private boolean isNumberValid(float num) {
        return this.previousNumbers.stream().noneMatch(existingNum -> Math.abs(existingNum.floatValue() - num) < this.minDifference);
    }

    @Generated
    public float getCurrent() {
        return this.current;
    }
}
