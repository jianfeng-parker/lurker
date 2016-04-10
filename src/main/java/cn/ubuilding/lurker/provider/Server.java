package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.common.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Wu Jianfeng
 * @since 16/4/2 07:20
 */

public final class Server {

    private Map<String, ProviderInfo> providerMap = new HashMap<String, ProviderInfo>();

    private int port;

    private boolean useSSL;

    public Server(List<ProviderInfo> providerInfos, int port, boolean useSSL) {
        if (null == providerInfos || providerInfos.size() == 0)
            throw new IllegalArgumentException("not found any service to provide");
        this.port = port;
        this.useSSL = useSSL;
        for (ProviderInfo providerInfo : providerInfos) {
            providerMap.put(providerInfo.getKey(), providerInfo);
        }
        if (useSSL) {
            // TODO nothing
        }
    }

    public Server(List<ProviderInfo> providerInfos, int port) {
        this(providerInfos, port, false);
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
                            ch.pipeline().addLast(new Decoder(Request.class))
                                    .addLast(new Encoder(Response.class))
                                    .addLast(new ServerHandler(providerMap));
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

//    private void initSslCtx() {
//        try {
//            if (ssl) {
//                SelfSignedCertificate ssc = new SelfSignedCertificate();
//                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
//            } else {
//                sslCtx = null;
//            }
//        } catch (Exception e) {
//            // TODO logging...
//        }
//    }
}
