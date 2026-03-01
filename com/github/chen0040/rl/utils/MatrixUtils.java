package com.github.chen0040.rl.utils;

import com.github.chen0040.rl.utils.Matrix;
import com.github.chen0040.rl.utils.Vec;
import java.util.List;

public class MatrixUtils {
    public static Matrix matrixFromColumnVectors(List<Vec> R) {
        int n = R.size();
        int m = R.get(0).getDimension();
        Matrix T = new Matrix(m, n);
        for (int c = 0; c < n; ++c) {
            Vec Rcol = R.get(c);
            for (int r : Rcol.getData().keySet()) {
                T.set(r, c, Rcol.get(r));
            }
        }
        return T;
    }
}
