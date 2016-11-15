package cn.ubuilding.rpc.client;

import cn.ubuilding.rpc.support.codec.Decoder;
import cn.ubuilding.rpc.support.codec.Encoder;
import cn.ubuilding.rpc.support.protocol.Request;
import cn.ubuilding.rpc.support.protocol.Response;
import cn.ubuilding.rpc.support.protocol.ResponseFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

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

    private CountDownLatch connectLatch = new CountDownLatch(1);

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
        this.handler = new ClientHandler();
        init();
    }

    public ResponseFuture send(Request request) {
        ResponseFuture future = new ResponseFuture(request);
        try {
            connectLatch.await();
        } catch (Exception e) {
            future.done(new Response(request.getId(), null, new ConnectTimeoutException("not yet connect to " + host + ":" + port)));
            return future;
        }
        this.handler.addFuture(future);
        channel.writeAndFlush(request);// 向服务端发送请求
        return future;
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

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));//.sync();
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        connectLatch.countDown();
                    }
                }
            });
        } catch (Exception ex) {
            logger.error("initialize connection to remote server() failure:" + ex.getMessage());
        }
    }


}
