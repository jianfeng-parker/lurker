package cn.ubuilding.lurker.http.core.processor;

import cn.ubuilding.lurker.http.annotation.RequestBody;
import cn.ubuilding.lurker.http.annotation.RequestParam;
import cn.ubuilding.lurker.http.exception.RequestHandleException;
import cn.ubuilding.lurker.http.support.ContentType;
import cn.ubuilding.lurker.http.support.utils.HttpUtils;
import cn.ubuilding.lurker.http.support.utils.BeanUtils;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 09:38
 */

public class PostRequestProcessor extends AbstractRequestProcessor {

    public PostRequestProcessor(Object controller, Method method) {
        super(controller, method);
    }

    public Object[] parameterArgs(Map<String, String> parameters, String requestBody, String contentType) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Annotation[] annotation = annotations[i];
            if (annotation == null || annotation.length == 0 || annotation[0] == null) {
                args[i] = type.isPrimitive() ? BeanUtils.defaultPrimitiveValue(type) : null;
            } else if (annotation[0] instanceof RequestParam) {
                args[i] = parseRequestParam((RequestParam) annotation[0], type, parameters);
            } else if (annotation[0] instanceof RequestBody) {
                if (((RequestBody) annotation[0]).required() && (requestBody == null || requestBody.length() == 0)) {
                    throw new RequestHandleException("request body is missing", HttpResponseStatus.BAD_REQUEST);
                }
                if (type.isPrimitive()) {
                    try {
                        args[i] = BeanUtils.getValue(requestBody, type);
                    } catch (Throwable t) {
                        throw new RequestHandleException(t.getMessage(), HttpResponseStatus.BAD_REQUEST);
                    }
                } else {
                    try {
                        args[i] = parse(contentType, requestBody, type);
                    } catch (RequestHandleException e) {
                        throw new RequestHandleException(e.getMessage(), e.getStatus());
                    } catch (Throwable t) {
                        throw new RequestHandleException(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
        }
        return args;
    }

    private <T> T parse(String contentType, String requestBody, Class<T> type) throws Exception {
        if (ContentType.Json.equalsIgnoreCase(contentType)) {
            return JSON.parseObject(requestBody, type);
        } else if (ContentType.FormUrlEncoded.equalsIgnoreCase(contentType)) {
            QueryStringDecoder decoder = new QueryStringDecoder(requestBody, false);
            Map<String, String> parameters = HttpUtils.getParameters(decoder.parameters());
            return BeanUtils.toBean(parameters, type);
        } else {
            throw new RequestHandleException("unsupported Content-Type", HttpResponseStatus.NOT_ACCEPTABLE);
        }
    }

}
