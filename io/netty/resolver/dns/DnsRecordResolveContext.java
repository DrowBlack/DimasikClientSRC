package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsResolveContext;
import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.util.ReferenceCountUtil;
import java.net.UnknownHostException;
import java.util.List;

final class DnsRecordResolveContext
extends DnsResolveContext<DnsRecord> {
    DnsRecordResolveContext(DnsNameResolver parent, DnsQuestion question, DnsRecord[] additionals, DnsServerAddressStream nameServerAddrs) {
        this(parent, question.name(), question.dnsClass(), new DnsRecordType[]{question.type()}, additionals, nameServerAddrs);
    }

    private DnsRecordResolveContext(DnsNameResolver parent, String hostname, int dnsClass, DnsRecordType[] expectedTypes, DnsRecord[] additionals, DnsServerAddressStream nameServerAddrs) {
        super(parent, hostname, dnsClass, expectedTypes, additionals, nameServerAddrs);
    }

    @Override
    DnsResolveContext<DnsRecord> newResolverContext(DnsNameResolver parent, String hostname, int dnsClass, DnsRecordType[] expectedTypes, DnsRecord[] additionals, DnsServerAddressStream nameServerAddrs) {
        return new DnsRecordResolveContext(parent, hostname, dnsClass, expectedTypes, additionals, nameServerAddrs);
    }

    @Override
    DnsRecord convertRecord(DnsRecord record, String hostname, DnsRecord[] additionals, EventLoop eventLoop) {
        return ReferenceCountUtil.retain(record);
    }

    @Override
    List<DnsRecord> filterResults(List<DnsRecord> unfiltered) {
        return unfiltered;
    }

    @Override
    void cache(String hostname, DnsRecord[] additionals, DnsRecord result, DnsRecord convertedResult) {
    }

    @Override
    void cache(String hostname, DnsRecord[] additionals, UnknownHostException cause) {
    }
}
