package cn.ubuilding.lurker.http.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 10:57
 */

public class RequestHandleException extends RuntimeException {

    private HttpResponseStatus status;

    public RequestHandleException(HttpResponseStatus status) {
        this(status.reasonPhrase(), status);
    }

    public RequestHandleException(String message, HttpResponseStatus status) {
        super(null == message || message.length() == 0 ? status.reasonPhrase() : message);
        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

}
