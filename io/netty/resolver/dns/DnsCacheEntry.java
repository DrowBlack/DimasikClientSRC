package io.netty.resolver.dns;

import java.net.InetAddress;

public interface DnsCacheEntry {
    public InetAddress address();

    public Throwable cause();
}
