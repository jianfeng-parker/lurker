package cn.ubuilding.lurker.http.core.processor;

import cn.ubuilding.lurker.http.annotation.RequestParam;
import cn.ubuilding.lurker.http.core.Render;
import cn.ubuilding.lurker.http.exception.RequestHandleException;
import cn.ubuilding.lurker.http.support.utils.BeanUtils;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2017/1/1 21:45
 */

public abstract class AbstractRequestProcessor implements RequestProcessor {

    protected Object controller;

    protected Method method;

    protected AbstractRequestProcessor(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Render process(Map<String, String> parameters, String requestBody, String contentType) {
        return null;
    }

    public Render process(Map<String, String> parameters, String requestBody) {
        return null;
    }

    public Render process(Map<String, String> parameters) {
        try {
            Object[] args = parameterArgs(parameters);
            return (Render) method.invoke(controller, args);
        } catch (RequestHandleException e) {
            return Render.text(e.getMessage(), e.getStatus());
        } catch (Throwable t) {
            return Render.text(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected Object[] parameterArgs(Map<String, String> parameters) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] parameterArgs = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Annotation[] annotation = annotations[i];
            if (annotation == null || annotation.length == 0 || !(annotation[0] instanceof RequestParam)) {
                parameterArgs[i] = type.isPrimitive() ? BeanUtils.defaultPrimitiveValue(type) : null;
            } else {
                RequestParam param = (RequestParam) annotation[0];
                String value = parameters.get(param.name());
                if (param.required() && (null == value || value.trim().length() == 0)) {
                    throw new RequestHandleException("parameter " + param.name() + " is required", HttpResponseStatus.BAD_REQUEST);
                }
                if (null != value && value.trim().length() > 0) {
                    try {
                        parameterArgs[i] = BeanUtils.getValue(value, type);
                    } catch (Exception e) {
                        throw new RequestHandleException("wrong parameter value for type", HttpResponseStatus.BAD_REQUEST);
                    }

                } else {
                    parameterArgs[i] = type.isPrimitive() ? BeanUtils.defaultPrimitiveValue(type) : null;
                }
            }
        }
        return parameterArgs;
    }


}
