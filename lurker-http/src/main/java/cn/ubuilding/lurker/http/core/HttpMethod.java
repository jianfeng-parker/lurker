package cn.ubuilding.lurker.http.core;

/**
 * @author Wu Jianfeng
 * @since 2016/12/24 16:05
 */

public enum HttpMethod {
    GET(io.netty.handler.codec.http.HttpMethod.GET), POST(io.netty.handler.codec.http.HttpMethod.POST),
    PUT(io.netty.handler.codec.http.HttpMethod.PUT), DELETE(io.netty.handler.codec.http.HttpMethod.DELETE);

    private io.netty.handler.codec.http.HttpMethod method;

    HttpMethod(io.netty.handler.codec.http.HttpMethod method) {
        this.method = method;
    }

    public io.netty.handler.codec.http.HttpMethod getMethod() {
        return method;
    }
}
