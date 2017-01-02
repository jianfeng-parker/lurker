package cn.ubuilding.lurker.http;

import cn.ubuilding.lurker.http.core.Controller;
import cn.ubuilding.lurker.http.core.router.Dispatcher;
import cn.ubuilding.lurker.http.core.router.Router;
import cn.ubuilding.lurker.http.support.utils.NetUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wu Jianfeng
 * @since 2016/11/15 22:49
 */

public final class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private int port = 8080; // default http port

    private String host;

    private boolean keepAlive = false;

    private int postSize = 1024 * 1024;

    private Router router;

    public HttpServer setPort(int port) {
        this.port = port;
        if (port <= NetUtils.MIN_PORT || port >= NetUtils.MAX_PORT) {
            throw new IllegalArgumentException("invalid port " + port);
        }
        return this;
    }

    /**
     * 设置接受Post请求体时的最大
     *
     * @param postSize post body大小
     */
    public HttpServer setPostSize(int postSize) {
        if (postSize <= 0) {
            throw new IllegalArgumentException("invalid post size:" + postSize);
        }
        this.postSize = postSize;
        return this;
    }

    public HttpServer setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    /**
     * 业务Controller
     */
    public HttpServer addActions(Controller... controllers) {
        this.router = new Router(controllers);
        return this;
    }

    public void start() {
        try {
            host = NetUtils.getLocalHost();
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("codec", new HttpServerCodec())
                                    .addLast("aggregator", new HttpObjectAggregator(postSize))
                                    .addLast("dispatcher", new Dispatcher(router, keepAlive));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, keepAlive)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture f = bootstrap.bind(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Throwable t) {
            logger.error("started http server on {}:{} error.", host, port, t);
        } finally {
            stop();
        }

    }

    public void stop() {
        if (null != workerGroup) {
            workerGroup.shutdownGracefully();
        }
        if (null != bossGroup) {
            bossGroup.shutdownGracefully();
        }
        if (null != router) {
            router.release();
        }
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public int getPostSize() {
        return postSize;
    }
}
