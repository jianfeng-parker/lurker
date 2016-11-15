package cn.ubuilding.lurker.rpc.server;

import cn.ubuilding.lurker.rpc.support.codec.Decoder;
import cn.ubuilding.lurker.rpc.support.codec.Encoder;
import cn.ubuilding.lurker.rpc.support.annotation.Rpc;
import cn.ubuilding.lurker.rpc.support.NetUtils;
import cn.ubuilding.lurker.rpc.support.protocol.Request;
import cn.ubuilding.lurker.rpc.support.protocol.Response;
import cn.ubuilding.lurker.rpc.support.registry.Registry;
import cn.ubuilding.lurker.rpc.support.registry.ZookeeperRegistry;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 15:44
 * <strong>
 * 需要发布成RPC服务的实现类上必须加上@Rpc标签，并在value中指定其 interface class
 * </strong>
 * <p>
 * 1. Server支持spring的方式指定RPC服务
 * Server Bean初始化时自动解析出RPC服务
 * <p>
 * 2. 为了尽量减少外部依赖(不依赖spring的相关jar) 通过Java代码指定RPC服务
 * 通过API {@link #addService(List)} 或者 {@link #addService(Object)} 指定需要暴露的RPC服务
 */

public class Server {//implements ApplicationContextAware, InitializingBean {

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
    private Map<String, Object> serviceMap = new HashMap<String, Object>();

    public Server() {
    }

    public Server(String registryAddress) {
        this(DEFAULT_PORT, registryAddress, 3000);
    }

    public Server(String registryAddress, int registryTimeout) {
        this(DEFAULT_PORT, registryAddress, registryTimeout);
    }

    public Server(int port, String registryAddress) {
        this(port, registryAddress, 3000);
    }

    public Server(int port, String registryAddress, int registryTimeout) {
        this(port, new ZookeeperRegistry(registryAddress, registryTimeout));
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

    /**
     * 添加需要发布的服务
     *
     * @param serviceImpls 服务实现类实例
     */
    public Server addService(List<Object> serviceImpls) {
        if (null != serviceImpls && serviceImpls.size() > 0) {
            for (Object object : serviceImpls) {
                addService(object);
            }
        }
        return this;
    }

    public Server addService(Object serviceImpl) {
        if (null == serviceImpl) {
            throw new NullPointerException("serviceImpl");
        }
        Rpc annotation = serviceImpl.getClass().getAnnotation(Rpc.class);
        if (null == annotation) {
            throw new IllegalStateException(serviceImpl + " is not rpc service instance");
        }
        Class<?> interfaceClass = annotation.value();
        if (!interfaceClass.isInstance(serviceImpl)) {
            throw new IllegalArgumentException(serviceImpl + " is not instance of " + interfaceClass);
        }
        serviceMap.put(interfaceClass.getName(), serviceImpl);
        return this;
    }

    public void start() {
        if (serviceMap.size() == 0) {
            throw new RuntimeException("not found any rpc services to start");
        }
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
                                        .addLast(new ServerHandler(serviceMap));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                String host = NetUtils.getLocalHost();
                ChannelFuture f = bootstrap.bind(host, port).sync();
                // 服务注册
                for (String serviceName : serviceMap.keySet()) {
                    registry.register(serviceName, host + NetUtils.ADDRESS_SEPARATOR + NetUtils.getAvailablePort(port));
                }
                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }

        } catch (Exception e) {
            logger.error("start rpc service failure:" + e.getMessage());
        }
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServices(List<Object> services) {
        addService(services);
    }
}
