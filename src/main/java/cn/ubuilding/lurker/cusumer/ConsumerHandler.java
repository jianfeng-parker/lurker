package cn.ubuilding.lurker.cusumer;

import io.netty.channel.*;

/**
 * @author Wu Jianfeng
 * @since 16/4/2 17:43
 */

public class ConsumerHandler extends ChannelInboundHandlerAdapter {


    public void channelActive(ChannelHandlerContext ctx) {
    }

    public void channelInactive(ChannelHandlerContext ctx) {

    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

//    private void generateTraffic() {
//        ctx.writeAndFlush(content.duplicate().retain()).addListener(trafficGenerator);
//    }
//
//    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
//        public void operationComplete(ChannelFuture future) {
//            if (future.isSuccess()) {
//                generateTraffic();
//            } else {
//                future.cause().printStackTrace();
//                future.channel().close();
//            }
//        }
//    };
}
