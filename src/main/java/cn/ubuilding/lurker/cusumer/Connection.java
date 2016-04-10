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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 16:45
 */

public class Connection {

    private Bootstrap bootstrap;

    private InetSocketAddress address;

    private ConsumerHandler handler;

    private volatile Channel channel;

    private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

    private boolean connected = false;

    public Connection(String host, int port) {
        if (null == host || host.trim().length() == 0 || port <= 0) {
            throw new IllegalArgumentException("invalid host(" + host + ") or port(" + port + ")");
        }
        this.address = new InetSocketAddress(host, port);
        this.handler = new ConsumerHandler();
        init();
    }


    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect(address);//.sync();
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        Channel c = future.channel();
                        channels.put(c.remoteAddress().toString(), c);
                        channel = c;
                        connected = true;
                    }
                }
            });
        } catch (Exception e) {
            // TODO logging...
        }
    }

    public Response send(Request request) {
        if (null == channel) {
            channel = getChannel(address.toString());
        }
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

    private Channel getChannel(String key) {
        return channels.get(key);
    }


}
