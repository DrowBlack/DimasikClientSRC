package com.github.chen0040.rl.utils;

import com.github.chen0040.rl.utils.TupleTwo;
import com.github.chen0040.rl.utils.Vec;
import java.util.ArrayList;
import java.util.List;

public class VectorUtils {
    public static List<Vec> removeZeroVectors(Iterable<Vec> vlist) {
        ArrayList<Vec> vstarlist = new ArrayList<Vec>();
        for (Vec v : vlist) {
            if (v.isZero()) continue;
            vstarlist.add(v);
        }
        return vstarlist;
    }

    public static TupleTwo<List<Vec>, List<Double>> normalize(Iterable<Vec> vlist) {
        ArrayList<Double> norms = new ArrayList<Double>();
        ArrayList<Vec> vstarlist = new ArrayList<Vec>();
        for (Vec v : vlist) {
            norms.add(v.norm(2));
            vstarlist.add(v.normalize());
        }
        return TupleTwo.create(vstarlist, norms);
    }
}
