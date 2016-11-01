package cn.ubuilding.lurker.client;

import cn.ubuilding.lurker.support.codec.Decoder;
import cn.ubuilding.lurker.support.codec.Encoder;
import cn.ubuilding.lurker.support.rpc.protocol.Request;
import cn.ubuilding.lurker.support.rpc.protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 16:45
 */

public final class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    private String host;

    private int port;

    private ClientHandler handler;

    private EventLoopGroup eventLoopGroup;

    private volatile Channel channel;

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
        this.handler = new ClientHandler();
        init();
    }

    public Response send(Request request) {
        if (null != channel) {
            ResponseFuture future = new ResponseFuture(request);
            this.handler.addFuture(future);
            channel.writeAndFlush(request);// 向服务端发送请求
            return future.get();
        } else {
            return null;
        }

    }

    public void close() {
        try {
            handler.close();
            eventLoopGroup.shutdownGracefully();
        } catch (Throwable ignored) {
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private void init() {
        try {
            eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new Decoder(Response.class));
                            channel.pipeline().addLast(new Encoder(Request.class));
                            channel.pipeline().addLast(handler);
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .option(ChannelOption.SO_REUSEADDR, true);
//                    .option(ChannelOption.AUTO_CLOSE, false);

            // TODO 此处有一个问题: 因为连接是异步的，所以有可能出现在连接还没有成功的情况下调用者就调用@see #send()方法了

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));//.sync();
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        channel = future.channel();
                    }
                }
            });
        } catch (Exception ex) {
            logger.error("initialize connection to remote server() failure:" + ex.getMessage());
        }
    }


}
