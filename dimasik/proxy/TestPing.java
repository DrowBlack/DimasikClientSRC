package dimasik.proxy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dimasik.proxy.Proxy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.NettyPacketEncoder;
import net.minecraft.network.NettyVarint21FrameDecoder;
import net.minecraft.network.NettyVarint21FrameEncoder;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.util.LazyValue;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class TestPing {
    public String state = "";
    private long pingSentAt;
    private NetworkManager pingDestination = null;
    private Proxy proxy;
    private static final ThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build());

    public void run(String ip, int port, Proxy proxy) {
        this.proxy = proxy;
        EXECUTOR.submit(() -> this.ping(ip, port));
    }

    private void ping(final String ip, int port) {
        NetworkManager networkManager;
        this.state = "Pinging " + ip + "...";
        try {
            networkManager = this.createTestNetworkManager(InetAddress.getByName(ip), port);
        }
        catch (UnknownHostException e) {
            this.state = String.valueOf((Object)TextFormatting.RED) + "Failed";
            return;
        }
        catch (Exception e) {
            this.state = String.valueOf((Object)TextFormatting.RED) + "Failed to ping";
            return;
        }
        this.pingDestination = networkManager;
        networkManager.setNetHandler(new IClientStatusNetHandler(){
            private boolean successful;

            @Override
            public void onDisconnect(ITextComponent reason) {
                TestPing.this.pingDestination = null;
                if (!this.successful) {
                    TestPing.this.state = String.valueOf((Object)TextFormatting.RED) + "Can't ping " + ip + ": " + reason.getString();
                }
            }

            @Override
            public NetworkManager getNetworkManager() {
                return networkManager;
            }

            @Override
            public void handleServerInfo(SServerInfoPacket packetIn) {
                TestPing.this.pingSentAt = Util.milliTime();
                networkManager.sendPacket(new CPingPacket(TestPing.this.pingSentAt));
            }

            @Override
            public void handlePong(SPongPacket packetIn) {
                this.successful = true;
                TestPing.this.pingDestination = null;
                long pingToServer = Util.milliTime() - TestPing.this.pingSentAt;
                TestPing.this.state = "Ping: " + pingToServer;
                networkManager.closeChannel(new TranslationTextComponent("multiplayer.status.finished"));
            }
        });
        try {
            networkManager.sendPacket(new CHandshakePacket(ip, port, ProtocolType.STATUS));
            networkManager.sendPacket(new CServerQueryPacket());
        }
        catch (Throwable throwable) {
            this.state = String.valueOf((Object)TextFormatting.RED) + "Can't ping " + ip;
        }
    }

    private NetworkManager createTestNetworkManager(InetAddress address, int port) {
        LazyValue<MultithreadEventLoopGroup> lazyvalue;
        Class oclass;
        final NetworkManager networkManager = new NetworkManager(PacketDirection.CLIENTBOUND);
        if (Epoll.isAvailable() && Minecraft.getInstance().gameSettings.isUsingNativeTransport()) {
            oclass = EpollSocketChannel.class;
            lazyvalue = NetworkManager.CLIENT_EPOLL_EVENTLOOP;
        } else {
            oclass = NioSocketChannel.class;
            lazyvalue = NetworkManager.CLIENT_NIO_EVENTLOOP;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(lazyvalue.getValue())).handler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException channelException) {
                    // empty catch block
                }
                channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new NettyVarint21FrameDecoder()).addLast("decoder", (ChannelHandler)new NettyPacketDecoder(PacketDirection.CLIENTBOUND)).addLast("prepender", (ChannelHandler)new NettyVarint21FrameEncoder()).addLast("encoder", (ChannelHandler)new NettyPacketEncoder(PacketDirection.SERVERBOUND)).addLast("packet_handler", (ChannelHandler)networkManager);
                if (TestPing.this.proxy.type == Proxy.ProxyType.SOCKS5) {
                    channel.pipeline().addFirst(new Socks5ProxyHandler(new InetSocketAddress(TestPing.this.proxy.getIp(), TestPing.this.proxy.getPort()), TestPing.this.proxy.username.isEmpty() ? null : TestPing.this.proxy.username, TestPing.this.proxy.password.isEmpty() ? null : TestPing.this.proxy.password));
                } else {
                    channel.pipeline().addFirst(new Socks4ProxyHandler(new InetSocketAddress(TestPing.this.proxy.getIp(), TestPing.this.proxy.getPort()), TestPing.this.proxy.username.isEmpty() ? null : TestPing.this.proxy.username));
                }
            }
        })).channel(oclass)).connect(address, port).syncUninterruptibly();
        return networkManager;
    }

    public void pingPendingNetworks() {
        if (this.pingDestination != null) {
            if (this.pingDestination.isChannelOpen()) {
                this.pingDestination.tick();
            } else {
                this.pingDestination.handleDisconnection();
            }
        }
    }
}
