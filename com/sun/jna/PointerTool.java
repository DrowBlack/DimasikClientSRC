package com.sun.jna;

import com.sun.jna.Pointer;

public class PointerTool {
    public static long getPeer(Pointer ptr) {
        return ptr.peer;
    }
}
