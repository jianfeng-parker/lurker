package cn.ubuilding.lurker.http.core.processor;

import cn.ubuilding.lurker.http.exception.RequestHandleException;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 09:36
 */

public class GetRequestProcessor extends AbstractRequestProcessor {

    public GetRequestProcessor(Object controller, Method method) {
        super(controller, method);
    }

    @Override
    public Object[] parameterArgs(Map<String, String> parameters, String requestBody, String contentType) {
        throw new RequestHandleException("cant handle request body with GET", HttpResponseStatus.METHOD_NOT_ALLOWED);
    }

}
