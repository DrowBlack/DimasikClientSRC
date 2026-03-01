package io.netty.util.internal;

import java.nio.ByteBuffer;

interface Cleaner {
    public void freeDirectBuffer(ByteBuffer var1);
}
