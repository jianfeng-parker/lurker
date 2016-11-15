package cn.ubuilding.lurker.rpc.client;

import cn.ubuilding.lurker.rpc.support.protocol.Response;
import cn.ubuilding.lurker.rpc.support.protocol.ResponseFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Wu Jianfeng
 * @since 16/4/2 17:43
 */

public final class ClientHandler extends ChannelInboundHandlerAdapter {

    private Map<String, ResponseFuture> futures = new ConcurrentHashMap<String, ResponseFuture>();

    private Channel channel;

    public void addFuture(ResponseFuture future) {
        if (null == future) throw new NullPointerException("ResponseFuture");
        futures.put(future.getRequest().getId(), future);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Response) {
            Response response = (Response) msg;
            String requestId = response.getRequestId();
            ResponseFuture future = futures.get(requestId);
            if (null != future) {
                futures.remove(requestId);
                future.done(response);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    public void close() {
        this.channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        this.channel.close();
    }

}
