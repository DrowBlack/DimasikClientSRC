package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UVTransformationUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EnumMap<Direction, TransformationMatrix> TRANSFORM_LOCAL_TO_GLOBAL = Util.make(Maps.newEnumMap(Direction.class), localToGlobalMap -> {
        localToGlobalMap.put(Direction.SOUTH, TransformationMatrix.identity());
        localToGlobalMap.put(Direction.EAST, new TransformationMatrix(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 90.0f, true), null, null));
        localToGlobalMap.put(Direction.WEST, new TransformationMatrix(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, true), null, null));
        localToGlobalMap.put(Direction.NORTH, new TransformationMatrix(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 180.0f, true), null, null));
        localToGlobalMap.put(Direction.UP, new TransformationMatrix(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -90.0f, true), null, null));
        localToGlobalMap.put(Direction.DOWN, new TransformationMatrix(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), 90.0f, true), null, null));
    });
    public static final EnumMap<Direction, TransformationMatrix> TRANSFORM_GLOBAL_TO_LOCAL = Util.make(Maps.newEnumMap(Direction.class), globalToLocalMap -> {
        for (Direction direction : Direction.values()) {
            globalToLocalMap.put(direction, TRANSFORM_LOCAL_TO_GLOBAL.get(direction).inverseVanilla());
        }
    });

    public static TransformationMatrix blockCenterToCorner(TransformationMatrix matrixIn) {
        Matrix4f matrix4f = Matrix4f.makeTranslate(0.5f, 0.5f, 0.5f);
        matrix4f.mul(matrixIn.getMatrix());
        matrix4f.mul(Matrix4f.makeTranslate(-0.5f, -0.5f, -0.5f));
        return new TransformationMatrix(matrix4f);
    }

    public static TransformationMatrix getUVLockTransform(TransformationMatrix matrixIn, Direction directionIn, Supplier<String> warningIn) {
        Direction direction = Direction.rotateFace(matrixIn.getMatrix(), directionIn);
        TransformationMatrix transformationmatrix = matrixIn.inverseVanilla();
        if (transformationmatrix == null) {
            LOGGER.warn(warningIn.get());
            return new TransformationMatrix(null, null, new Vector3f(0.0f, 0.0f, 0.0f), null);
        }
        TransformationMatrix transformationmatrix1 = TRANSFORM_GLOBAL_TO_LOCAL.get(directionIn).composeVanilla(transformationmatrix).composeVanilla(TRANSFORM_LOCAL_TO_GLOBAL.get(direction));
        return UVTransformationUtil.blockCenterToCorner(transformationmatrix1);
    }
}
