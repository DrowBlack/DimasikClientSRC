package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.handshake.ClientHandshakeNetHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.LegacyPingHandler;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.NettyPacketEncoder;
import net.minecraft.network.NettyVarint21FrameDecoder;
import net.minecraft.network.NettyVarint21FrameEncoder;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.RateLimitedNetworkManager;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final LazyValue<NioEventLoopGroup> SERVER_NIO_EVENTLOOP = new LazyValue<NioEventLoopGroup>(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build()));
    public static final LazyValue<EpollEventLoopGroup> SERVER_EPOLL_EVENTLOOP = new LazyValue<EpollEventLoopGroup>(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build()));
    private final MinecraftServer server;
    public volatile boolean isAlive;
    private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

    public NetworkSystem(MinecraftServer server) {
        this.server = server;
        this.isAlive = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addEndpoint(@Nullable InetAddress address, int port) throws IOException {
        List<ChannelFuture> list = this.endpoints;
        synchronized (list) {
            LazyValue<MultithreadEventLoopGroup> lazyvalue;
            Class oclass;
            if (Epoll.isAvailable() && this.server.shouldUseNativeTransport()) {
                oclass = EpollServerSocketChannel.class;
                lazyvalue = SERVER_EPOLL_EVENTLOOP;
                LOGGER.info("Using epoll channel type");
            } else {
                oclass = NioServerSocketChannel.class;
                lazyvalue = SERVER_NIO_EVENTLOOP;
                LOGGER.info("Using default channel type");
            }
            this.endpoints.add(((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(oclass)).childHandler(new ChannelInitializer<Channel>(){

                @Override
                protected void initChannel(Channel p_initChannel_1_) throws Exception {
                    try {
                        p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                    }
                    catch (ChannelException channelException) {
                        // empty catch block
                    }
                    p_initChannel_1_.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("legacy_query", (ChannelHandler)new LegacyPingHandler(NetworkSystem.this)).addLast("splitter", (ChannelHandler)new NettyVarint21FrameDecoder()).addLast("decoder", (ChannelHandler)new NettyPacketDecoder(PacketDirection.SERVERBOUND)).addLast("prepender", (ChannelHandler)new NettyVarint21FrameEncoder()).addLast("encoder", (ChannelHandler)new NettyPacketEncoder(PacketDirection.CLIENTBOUND));
                    int i = NetworkSystem.this.server.func_241871_k();
                    NetworkManager networkmanager = i > 0 ? new RateLimitedNetworkManager(i) : new NetworkManager(PacketDirection.SERVERBOUND);
                    NetworkSystem.this.networkManagers.add(networkmanager);
                    p_initChannel_1_.pipeline().addLast("packet_handler", (ChannelHandler)networkmanager);
                    networkmanager.setNetHandler(new ServerHandshakeNetHandler(NetworkSystem.this.server, networkmanager));
                }
            }).group(lazyvalue.getValue()).localAddress(address, port)).bind().syncUninterruptibly());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SocketAddress addLocalEndpoint() {
        ChannelFuture channelfuture;
        List<ChannelFuture> list = this.endpoints;
        synchronized (list) {
            channelfuture = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>(){

                @Override
                protected void initChannel(Channel p_initChannel_1_) throws Exception {
                    NetworkManager networkmanager = new NetworkManager(PacketDirection.SERVERBOUND);
                    networkmanager.setNetHandler(new ClientHandshakeNetHandler(NetworkSystem.this.server, networkmanager));
                    NetworkSystem.this.networkManagers.add(networkmanager);
                    p_initChannel_1_.pipeline().addLast("packet_handler", (ChannelHandler)networkmanager);
                }
            }).group(SERVER_NIO_EVENTLOOP.getValue()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
            this.endpoints.add(channelfuture);
        }
        return channelfuture.channel().localAddress();
    }

    public void terminateEndpoints() {
        this.isAlive = false;
        for (ChannelFuture channelfuture : this.endpoints) {
            try {
                channelfuture.channel().close().sync();
            }
            catch (InterruptedException interruptedexception) {
                LOGGER.error("Interrupted whilst closing channel");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List<NetworkManager> list = this.networkManagers;
        synchronized (list) {
            Iterator<NetworkManager> iterator = this.networkManagers.iterator();
            while (iterator.hasNext()) {
                NetworkManager networkmanager = iterator.next();
                if (networkmanager.hasNoChannel()) continue;
                if (networkmanager.isChannelOpen()) {
                    try {
                        networkmanager.tick();
                    }
                    catch (Exception exception) {
                        if (networkmanager.isLocalChannel()) {
                            throw new ReportedException(CrashReport.makeCrashReport(exception, "Ticking memory connection"));
                        }
                        LOGGER.warn("Failed to handle packet for {}", (Object)networkmanager.getRemoteAddress(), (Object)exception);
                        StringTextComponent itextcomponent = new StringTextComponent("Internal server error");
                        networkmanager.sendPacket(new SDisconnectPacket(itextcomponent), p_210474_2_ -> networkmanager.closeChannel(itextcomponent));
                        networkmanager.disableAutoRead();
                    }
                    continue;
                }
                iterator.remove();
                networkmanager.handleDisconnection();
            }
        }
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}
