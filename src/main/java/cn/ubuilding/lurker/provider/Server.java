package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.codec.Decoder;
import cn.ubuilding.lurker.codec.Encoder;
import cn.ubuilding.lurker.protocol.Request;
import cn.ubuilding.lurker.protocol.Response;
import cn.ubuilding.lurker.provider.registry.DefaultRegister;
import cn.ubuilding.lurker.provider.registry.Register;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 15:44
 */

public final class Server {

    /**
     * 用于将服务信息发布到注册中心
     */
    private Register register;

    /**
     * RPC服务端口
     */
    private int port;

    /**
     * RPC服务地址
     */
    private String host;

    /**
     * RPC服务提供者列表
     */
    private List<Provider> providers;

    private Map<String, Provider> providerMap = new HashMap<String, Provider>();

    public Server(String host, List<Provider> providers, String registryAddress) {
        this(host, 8899, providers, registryAddress);
    }

    public Server(String host, int port, List<Provider> providers, String registryAddress) {
        this(host, port, providers, new DefaultRegister(host, port, providers, registryAddress));
    }

    public Server(String host, int port, List<Provider> providers, Register register) {
        this.host = host;
        this.port = port;
        this.providers = providers;
        this.register = register;
    }

    /**
     * RPC服务启动入口
     */
    public void start() {
        try {
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

                register.publish(); // 发布服务

                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }

        } catch (Exception e) {
            System.out.println("publish  failure:" + e.getMessage());
        }
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

}
