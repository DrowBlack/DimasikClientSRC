package org.joml.sampling;

import org.joml.Random;
import org.joml.sampling.Callback2d;
import org.joml.sampling.Math;

public class SpiralSampling {
    private final Random rnd;

    public SpiralSampling(long seed) {
        this.rnd = new Random(seed);
    }

    public void createEquiAngle(float radius, int numRotations, int numSamples, Callback2d callback) {
        for (int sample = 0; sample < numSamples; ++sample) {
            float angle = (float)java.lang.Math.PI * 2 * (float)(sample * numRotations) / (float)numSamples;
            float r = radius * (float)sample / (float)(numSamples - 1);
            float x = (float)Math.sin_roquen_9(angle + 1.5707964f) * r;
            float y = (float)Math.sin_roquen_9(angle) * r;
            callback.onNewSample(x, y);
        }
    }

    public void createEquiAngle(float radius, int numRotations, int numSamples, float jitter, Callback2d callback) {
        float spacing = radius / (float)numRotations;
        for (int sample = 0; sample < numSamples; ++sample) {
            float angle = (float)java.lang.Math.PI * 2 * (float)(sample * numRotations) / (float)numSamples;
            float r = radius * (float)sample / (float)(numSamples - 1) + (this.rnd.nextFloat() * 2.0f - 1.0f) * spacing * jitter;
            float x = (float)Math.sin_roquen_9(angle + 1.5707964f) * r;
            float y = (float)Math.sin_roquen_9(angle) * r;
            callback.onNewSample(x, y);
        }
    }
}
