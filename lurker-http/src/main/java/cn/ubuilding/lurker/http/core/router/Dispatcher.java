package cn.ubuilding.lurker.http.core.router;

import cn.ubuilding.lurker.http.core.Render;
import cn.ubuilding.lurker.http.core.processor.RequestProcessor;
import cn.ubuilding.lurker.http.support.HttpUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2016/11/27 20:58
 */

public class Dispatcher extends ChannelInboundHandlerAdapter {

    private Router router;

    private boolean keepAlive;

    public Dispatcher(Router router, boolean keepAlive) {
        if (null == router) {
            throw new NullPointerException("router");
        }
        this.router = router;
        this.keepAlive = keepAlive;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            Render render;
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpMethod method = request.method();
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            String path = decoder.path();
            RequestProcessor processor = router.getProcessor(method, path);
            if (null == processor) {
                render = Render.text(HttpResponseStatus.NOT_FOUND);
                ctx.write(render.rendering()).addListener(ChannelFutureListener.CLOSE);
            } else {
                Map<String, String> parameters = HttpUtils.getParameters(decoder.parameters());
                if (HttpMethod.GET.equals(method)) {
                    render = processor.process(parameters);
                    response(ctx, render);
                } else { // Get以外请求支持request body
                    String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
                    render = processor.process(parameters, request.content().toString(Charset.defaultCharset()), contentType);
                    response(ctx, render);
                }
            }
        }
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void response(ChannelHandlerContext ctx, Render render) {
        FullHttpResponse response = render.rendering();
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.write(response);
        }
    }


}
