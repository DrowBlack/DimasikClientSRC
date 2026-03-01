package net.minecraft.client.renderer.model;

import net.minecraft.util.math.vector.TransformationMatrix;

public interface IModelTransform {
    default public TransformationMatrix getRotation() {
        return TransformationMatrix.identity();
    }

    default public boolean isUvLock() {
        return false;
    }
}
