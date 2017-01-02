package cn.ubuilding.lurker.http.core;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

/**
 * @author Wu Jianfeng
 * @since 2016/12/4 10:54
 */

public class Render {

    private String data;

    private RenderType type;

    private HttpResponseStatus status;

    public Render(String data, RenderType type) {
        this(data, type, HttpResponseStatus.OK);
    }

    public Render(String data, RenderType type, HttpResponseStatus status) {
        this.data = data;
        this.type = type;
        this.status = status;
    }

    public FullHttpResponse rendering() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(data.getBytes()));
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        switch (type) {
            case JSON:
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/x-json;charset=UTF-8");
                break;
            case XML:
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/xml;charset=UTF-8");
                break;
            case TEXT:
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                break;
            default:
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
//                response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public static Render text(String text, HttpResponseStatus status) {
        return new Render(text, RenderType.TEXT, status);
    }

    public static Render text(HttpResponseStatus status) {
        return text(status.reasonPhrase(), status);
    }

}
