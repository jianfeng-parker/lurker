package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.common.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Wu Jianfeng
 * @since 16/4/2 07:20
 */

public final class Server {

    private SslContext sslCtx;

    private Map<String, Provider> providerMap = new HashMap<String, Provider>();

    private boolean ssl;

    private int port;

    public Server(List<Provider> providers, boolean ssl, int port) throws Exception {
        if (null == providers || providers.size() == 0)
            throw new IllegalArgumentException("not found any service to provide");
        for (Provider provider : providers) {
            providerMap.put(provider.getKey(), provider);
        }
        this.ssl = ssl;
        this.port = port;
        initSslCtx();
    }

    public Server(List<Provider> providers) throws Exception {
        if (null == providers || providers.size() == 0)
            throw new IllegalArgumentException("not found any service to provide");
        for (Provider provider : providers) {
            providerMap.put(provider.getKey(), provider);
        }
        LurkerConfig config = LurkerConfig.getInstance();
        this.ssl = null != config.getProperty("use.ssl") && Boolean.parseBoolean(config.getProperty("use.ssl"));
        String portConfig = config.getProperty("rpc.port");
        this.port = null != portConfig ? Integer.parseInt(portConfig) : 8899;
        initSslCtx();
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            p.addLast(new Decoder(Request.class));
                            p.addLast(new Encoder(Response.class));
                            p.addLast(new ProviderHandler(providerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = bootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public boolean isSsl() {
        return ssl;
    }

    public int getPort() {
        return port;
    }

    private void initSslCtx() {
        try {
            if (ssl) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }
        } catch (Exception e) {
            // TODO logging...
        }
    }
}
