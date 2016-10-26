package cn.ubuilding.lurker.server;

import cn.ubuilding.lurker.support.codec.Decoder;
import cn.ubuilding.lurker.support.codec.Encoder;
import cn.ubuilding.lurker.support.annotation.RpcService;
import cn.ubuilding.lurker.support.NetUtils;
import cn.ubuilding.lurker.support.rpc.protocol.Request;
import cn.ubuilding.lurker.support.rpc.protocol.Response;
import cn.ubuilding.lurker.support.registry.Registry;
import cn.ubuilding.lurker.support.registry.ZookeeperRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 15:44
 */

public final class Server implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    /**
     * RPC服务默认端口
     */
    private static final int DEFAULT_PORT = 8899;

    /**
     * 用于将服务信息发布到注册中心
     */
    private Registry registry;

    /**
     * RPC服务端口
     */
    private int port;

    /**
     * 存放RPC服务接口及其实现类
     */
    private Map<String, Object> services = new HashMap<String, Object>();

    public Server(String registryAddress) {
        this(DEFAULT_PORT, registryAddress);
    }

    public Server(int port, String registryAddress) {
        this(port, new ZookeeperRegistry(registryAddress));
    }

    public Server(Registry registry) {
        this(DEFAULT_PORT, registry);
    }

    public Server(int port, Registry registry) {
        if (port < NetUtils.MIN_PORT || port > NetUtils.MAX_PORT) {
            throw new IllegalArgumentException("invalid port");
        }
        if (null == registry) {
            throw new NullPointerException("registry");
        }
        registry.check();
        this.port = port;
        this.registry = registry;
    }

    public int getPort() {
        return port;
    }


    public void setPort(int port) {
        this.port = port;
    }


    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> beans = ctx.getBeansWithAnnotation(RpcService.class);
        if (null != beans && beans.size() > 0) {
            for (Object bean : beans.values()) {
                String serviceName = bean.getClass().getAnnotation(RpcService.class).value().getName();
                services.put(serviceName, bean);
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
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
                                ch.pipeline()
                                        .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                        .addLast(new Decoder(Request.class))
                                        .addLast(new Encoder(Response.class))
                                        .addLast(new ServerHandler(services));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                String host = NetUtils.getLocalHost();
                ChannelFuture f = bootstrap.bind(host, port).sync();
                // 服务注册
                if (null != services && services.size() > 0) {
                    for (String serviceName : services.keySet()) {
                        registry.register(serviceName, host + NetUtils.ADDRESS_SEPARATOR + NetUtils.getAvailablePort(port));
                    }
                }
                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }

        } catch (Exception e) {
            logger.error("register  failure:" + e.getMessage());
        }
    }
}
