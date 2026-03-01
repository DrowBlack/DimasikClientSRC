package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.resolver.dns.DnsCache;
import io.netty.resolver.dns.DnsCacheEntry;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultDnsCache
implements DnsCache {
    private final ConcurrentMap<String, Entries> resolveCache = PlatformDependent.newConcurrentHashMap();
    private static final int MAX_SUPPORTED_TTL_SECS = (int)TimeUnit.DAYS.toSeconds(730L);
    private final int minTtl;
    private final int maxTtl;
    private final int negativeTtl;

    public DefaultDnsCache() {
        this(0, MAX_SUPPORTED_TTL_SECS, 0);
    }

    public DefaultDnsCache(int minTtl, int maxTtl, int negativeTtl) {
        this.minTtl = Math.min(MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(minTtl, "minTtl"));
        this.maxTtl = Math.min(MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(maxTtl, "maxTtl"));
        if (minTtl > maxTtl) {
            throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)");
        }
        this.negativeTtl = ObjectUtil.checkPositiveOrZero(negativeTtl, "negativeTtl");
    }

    public int minTtl() {
        return this.minTtl;
    }

    public int maxTtl() {
        return this.maxTtl;
    }

    public int negativeTtl() {
        return this.negativeTtl;
    }

    @Override
    public void clear() {
        while (!this.resolveCache.isEmpty()) {
            Iterator i = this.resolveCache.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = i.next();
                i.remove();
                ((Entries)e.getValue()).clearAndCancel();
            }
        }
    }

    @Override
    public boolean clear(String hostname) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        Entries entries = (Entries)this.resolveCache.remove(hostname);
        return entries != null && entries.clearAndCancel();
    }

    private static boolean emptyAdditionals(DnsRecord[] additionals) {
        return additionals == null || additionals.length == 0;
    }

    @Override
    public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        if (!DefaultDnsCache.emptyAdditionals(additionals)) {
            return Collections.emptyList();
        }
        Entries entries = (Entries)this.resolveCache.get(hostname);
        return entries == null ? null : (List)entries.get();
    }

    @Override
    public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(address, "address");
        ObjectUtil.checkNotNull(loop, "loop");
        DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, address);
        if (this.maxTtl == 0 || !DefaultDnsCache.emptyAdditionals(additionals)) {
            return e;
        }
        this.cache0(e, Math.max(this.minTtl, Math.min(MAX_SUPPORTED_TTL_SECS, (int)Math.min((long)this.maxTtl, originalTtl))), loop);
        return e;
    }

    @Override
    public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(cause, "cause");
        ObjectUtil.checkNotNull(loop, "loop");
        DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, cause);
        if (this.negativeTtl == 0 || !DefaultDnsCache.emptyAdditionals(additionals)) {
            return e;
        }
        this.cache0(e, Math.min(MAX_SUPPORTED_TTL_SECS, this.negativeTtl), loop);
        return e;
    }

    private void cache0(DefaultDnsCacheEntry e, int ttl, EventLoop loop) {
        Entries entries = (Entries)this.resolveCache.get(e.hostname());
        if (entries == null) {
            entries = new Entries(e);
            Entries oldEntries = this.resolveCache.putIfAbsent(e.hostname(), entries);
            if (oldEntries != null) {
                entries = oldEntries;
            }
        }
        entries.add(e);
        this.scheduleCacheExpiration(e, ttl, loop);
    }

    private void scheduleCacheExpiration(final DefaultDnsCacheEntry e, int ttl, EventLoop loop) {
        e.scheduleExpiration(loop, new Runnable(){

            @Override
            public void run() {
                Entries entries = (Entries)DefaultDnsCache.this.resolveCache.remove(e.hostname);
                if (entries != null) {
                    entries.clearAndCancel();
                }
            }
        }, ttl, TimeUnit.SECONDS);
    }

    public String toString() {
        return "DefaultDnsCache(minTtl=" + this.minTtl + ", maxTtl=" + this.maxTtl + ", negativeTtl=" + this.negativeTtl + ", cached resolved hostname=" + this.resolveCache.size() + ")";
    }

    private static final class Entries
    extends AtomicReference<List<DefaultDnsCacheEntry>> {
        Entries(DefaultDnsCacheEntry entry) {
            super(Collections.singletonList(entry));
        }

        void add(DefaultDnsCacheEntry e) {
            if (e.cause() == null) {
                while (true) {
                    List entries;
                    if (!(entries = (List)this.get()).isEmpty()) {
                        DefaultDnsCacheEntry firstEntry = (DefaultDnsCacheEntry)entries.get(0);
                        if (firstEntry.cause() != null) {
                            assert (entries.size() == 1);
                            if (!this.compareAndSet(entries, Collections.singletonList(e))) continue;
                            firstEntry.cancelExpiration();
                            return;
                        }
                        ArrayList<DefaultDnsCacheEntry> newEntries = new ArrayList<DefaultDnsCacheEntry>(entries.size() + 1);
                        DefaultDnsCacheEntry replacedEntry = null;
                        for (int i = 0; i < entries.size(); ++i) {
                            DefaultDnsCacheEntry entry = (DefaultDnsCacheEntry)entries.get(i);
                            if (!e.address().equals(entry.address())) {
                                newEntries.add(entry);
                                continue;
                            }
                            assert (replacedEntry == null);
                            replacedEntry = entry;
                        }
                        newEntries.add(e);
                        if (!this.compareAndSet(entries, newEntries)) continue;
                        if (replacedEntry != null) {
                            replacedEntry.cancelExpiration();
                        }
                        return;
                    }
                    if (this.compareAndSet(entries, Collections.singletonList(e))) break;
                }
                return;
            }
            List<DefaultDnsCacheEntry> entries = this.getAndSet(Collections.singletonList(e));
            Entries.cancelExpiration(entries);
        }

        boolean clearAndCancel() {
            List<DefaultDnsCacheEntry> entries = this.getAndSet(Collections.emptyList());
            if (entries.isEmpty()) {
                return false;
            }
            Entries.cancelExpiration(entries);
            return true;
        }

        private static void cancelExpiration(List<DefaultDnsCacheEntry> entryList) {
            int numEntries = entryList.size();
            for (int i = 0; i < numEntries; ++i) {
                entryList.get(i).cancelExpiration();
            }
        }
    }

    private static final class DefaultDnsCacheEntry
    implements DnsCacheEntry {
        private final String hostname;
        private final InetAddress address;
        private final Throwable cause;
        private volatile ScheduledFuture<?> expirationFuture;

        DefaultDnsCacheEntry(String hostname, InetAddress address) {
            this.hostname = ObjectUtil.checkNotNull(hostname, "hostname");
            this.address = ObjectUtil.checkNotNull(address, "address");
            this.cause = null;
        }

        DefaultDnsCacheEntry(String hostname, Throwable cause) {
            this.hostname = ObjectUtil.checkNotNull(hostname, "hostname");
            this.cause = ObjectUtil.checkNotNull(cause, "cause");
            this.address = null;
        }

        @Override
        public InetAddress address() {
            return this.address;
        }

        @Override
        public Throwable cause() {
            return this.cause;
        }

        String hostname() {
            return this.hostname;
        }

        void scheduleExpiration(EventLoop loop, Runnable task, long delay, TimeUnit unit) {
            assert (this.expirationFuture == null) : "expiration task scheduled already";
            this.expirationFuture = loop.schedule(task, delay, unit);
        }

        void cancelExpiration() {
            ScheduledFuture<?> expirationFuture = this.expirationFuture;
            if (expirationFuture != null) {
                expirationFuture.cancel(false);
            }
        }

        public String toString() {
            if (this.cause != null) {
                return this.hostname + '/' + this.cause;
            }
            return this.address.toString();
        }
    }
}
