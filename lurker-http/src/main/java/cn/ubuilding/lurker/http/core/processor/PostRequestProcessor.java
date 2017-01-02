package cn.ubuilding.lurker.http.core.processor;

import cn.ubuilding.lurker.http.annotation.RequestBody;
import cn.ubuilding.lurker.http.annotation.RequestParam;
import cn.ubuilding.lurker.http.core.Render;
import cn.ubuilding.lurker.http.exception.RequestHandleException;
import cn.ubuilding.lurker.http.support.ContentType;
import cn.ubuilding.lurker.http.support.HttpUtils;
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

    public Render process(Map<String, String> parameters, String requestBody, String contentType) {
        try {
            Object[] args = parameterArgs(parameters, requestBody, contentType);
            return (Render) method.invoke(controller, args);
        } catch (RequestHandleException e) {
            return Render.text(e.getMessage(), e.getStatus());
        } catch (Throwable t) {
            return Render.text(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);

        }
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
                RequestParam param = (RequestParam) annotation[0];
                String value = parameters.get(param.name());
                if (param.required() && (null == value || value.trim().length() == 0)) {
                    throw new RequestHandleException("parameter " + param.name() + " is required", HttpResponseStatus.BAD_REQUEST);
                }
                if (null != value && value.trim().length() > 0) {
                    try {
                        args[i] = type.isInstance(value) ? value : BeanUtils.getValue(value, type);
                    } catch (Exception e) {
                        throw new RequestHandleException("wrong parameter value for type", HttpResponseStatus.BAD_REQUEST);
                    }
                } else {
                    args[i] = type.isPrimitive() ? BeanUtils.defaultPrimitiveValue(type) : null;
                }
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
