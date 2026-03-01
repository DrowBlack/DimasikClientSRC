package org.openjdk.nashorn.internal.runtime.regexp.joni;

import org.openjdk.nashorn.internal.runtime.regexp.joni.ScanEnvironment;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.CClassNode;
import org.openjdk.nashorn.internal.runtime.regexp.joni.ast.ConsAltNode;

public final class ApplyCaseFoldArg {
    final ScanEnvironment env;
    final CClassNode cc;
    ConsAltNode altRoot;
    ConsAltNode tail;

    public ApplyCaseFoldArg(ScanEnvironment env, CClassNode cc) {
        this.env = env;
        this.cc = cc;
    }
}
