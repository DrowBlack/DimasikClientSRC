package org.openjdk.nashorn.internal.runtime.regexp.joni;

import org.openjdk.nashorn.internal.runtime.regexp.joni.MinMaxLen;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ScanEnvironment;

final class OptEnvironment {
    final MinMaxLen mmd = new MinMaxLen();
    int options;
    int caseFoldFlag;
    ScanEnvironment scanEnv;

    OptEnvironment() {
    }

    void copy(OptEnvironment other) {
        this.mmd.copy(other.mmd);
        this.options = other.options;
        this.caseFoldFlag = other.caseFoldFlag;
        this.scanEnv = other.scanEnv;
    }
}
