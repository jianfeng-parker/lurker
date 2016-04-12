package cn.ubuilding.lurker.cusumer;

import cn.ubuilding.lurker.common.Decoder;
import cn.ubuilding.lurker.common.Encoder;
import cn.ubuilding.lurker.common.Request;
import cn.ubuilding.lurker.common.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 16:45
 */

public final class Connection {

    private Bootstrap bootstrap;

    private InetSocketAddress address;

    private ConsumerHandler handler;

    private volatile Channel channel;

    private boolean connected = false;

    public Connection(InetSocketAddress address) {
        this.address = address;
        this.handler = new ConsumerHandler();
        init();
    }

    /**
     * 创建当前Connection实例与服务端的连接
     * 并保存连接中与服务端的Channel对象
     * // TODO 此处有一个问题: 因为连接是异步的，所以有可能出现在连接还没有成功的情况下调用者就调用@see #send()方法了
     */
    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect(address);//.sync();
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        connected = true;
                    }
                }
            });
        } catch (Exception e) {
            // TODO logging...
        }
    }

    public Response send(Request request) {
        if (null != channel) {
            ResponseFuture future = new ResponseFuture(request);
            this.handler.addFuture(future);
            channel.writeAndFlush(request);// 向客户端发送请求
            return future.get();
        } else {
            return null;
        }

    }

    public String getHost() {
        return address.getHostName();
    }

    public int getPort() {
        return address.getPort();
    }

    public boolean isConnected() {
        return connected;
    }

    private void init() {
        try {
            EventLoopGroup group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
