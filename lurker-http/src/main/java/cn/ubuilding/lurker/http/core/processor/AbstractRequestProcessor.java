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
        try {
            return doProcess(parameterArgs(parameters, requestBody, contentType));
        } catch (RequestHandleException e) {
            return Render.text(e.getMessage(), e.getStatus());
        }
    }

    public Render process(Map<String, String> parameters) {
        try {
            return doProcess(parameterArgs(parameters));
        } catch (RequestHandleException e) {
            return Render.text(e.getMessage(), e.getStatus());
        }
    }

    protected Object[] parameterArgs(Map<String, String> parameters) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Annotation[] annotation = annotations[i];
            if (annotation == null || annotation.length == 0 || !(annotation[0] instanceof RequestParam)) {
                args[i] = type.isPrimitive() ? BeanUtils.defaultPrimitiveValue(type) : null;
            } else {
                args[i] = parseRequestParam((RequestParam) annotation[0], type, parameters);
            }
        }
        return args;
    }

    protected abstract Object[] parameterArgs(Map<String, String> parameters, String requestBody, String contentType);

    private Render doProcess(Object[] args) {
        try {
            return (Render) method.invoke(controller, args);
        } catch (Throwable t) {
            return Render.text(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected Object parseRequestParam(RequestParam annotation, Class<?> type, Map<String, String> parameters) {
        String value = parameters.get(annotation.name());
        if (annotation.required() && (null == value || value.trim().length() == 0)) {
            throw new RequestHandleException("parameter " + annotation.name() + " is required", HttpResponseStatus.BAD_REQUEST);
        }
        if (null != value && value.trim().length() > 0) {
            try {
                return type.isInstance(value) ? value : BeanUtils.getValue(value, type);
            } catch (Exception e) {
                throw new RequestHandleException("wrong parameter value for type", HttpResponseStatus.BAD_REQUEST);
            }

        } else {
            return type.isPrimitive() ? BeanUtils.defaultPrimitiveValue(type) : null;
        }
    }


}
