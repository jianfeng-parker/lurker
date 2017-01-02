package cn.ubuilding.lurker.http.core;

import cn.ubuilding.lurker.http.annotation.RequestBody;
import cn.ubuilding.lurker.http.annotation.RequestParam;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2016/12/4 11:05
 */

public class PostRequestProcessor {

    private Object controller;

    private Method method;

    public PostRequestProcessor(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Render handle(Map<String, String> parameters) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] parameterValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Annotation[] annotation = annotations[i];
            if (annotation == null || annotation.length == 0 || !(annotation[0] instanceof RequestParam)) {
                parameterValues[i] = type.isPrimitive() ? primitiveDefaults.get(type) : null;
            } else {
                RequestParam param = (RequestParam) annotation[0];
                String value = parameters.get(param.name());
                if (param.required() && (null == value || value.trim().length() == 0)) {
                    return new Render("parameter " + param.name() + " is required", RenderType.TEXT, HttpResponseStatus.BAD_REQUEST);
                }
                if (null != value && value.trim().length() > 0) {
                    try {
                        parameterValues[i] = doConvert(value, type);
                    } catch (Exception e) {
                        return new Render("wrong parameter value for type", RenderType.TEXT, HttpResponseStatus.BAD_REQUEST);
                    }

                } else {
                    parameterValues[i] = type.isPrimitive() ? primitiveDefaults.get(type) : null;
                }
            }
        }
        try {
            return (Render) method.invoke(controller, parameterValues);
        } catch (Throwable t) {
            return Render.text(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Render handle(Map<String, String> parameters, String requestBody) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] parameterValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            Annotation[] annotation = annotations[i];
            if (annotation == null || annotation.length == 0 || annotation[0] == null) {
                parameterValues[i] = type.isPrimitive() ? primitiveDefaults.get(type) : null;
            } else if (annotation[0] instanceof RequestParam) {
                RequestParam param = (RequestParam) annotation[0];
                String value = parameters.get(param.name());
                if (param.required() && (null == value || value.trim().length() == 0)) {
                    return new Render("parameter " + param.name() + " is required", RenderType.TEXT, HttpResponseStatus.BAD_REQUEST);
                }
                if (null != value && value.trim().length() > 0) {
                    try {
                        parameterValues[i] = type.isInstance(value) ? value : doConvert(value, type);
                    } catch (Exception e) {
                        return Render.text(e.getMessage(), HttpResponseStatus.BAD_REQUEST);
                    }
                } else {
                    parameterValues[i] = type.isPrimitive() ? primitiveDefaults.get(type) : null;
                }
            } else if (annotation[0] instanceof RequestBody) {
                if (((RequestBody) annotation[0]).required() && (requestBody == null || requestBody.length() == 0)) {
                    return Render.text("request body is null", HttpResponseStatus.BAD_REQUEST);
                }
                if (type.isPrimitive()) {
                    try {
                        parameterValues[i] = doConvert(requestBody, type);
                    } catch (Throwable t) {
                        return Render.text(t.getMessage(), HttpResponseStatus.BAD_REQUEST);
                    }
                } else {
                    try {
                        parameterValues[i] = requestBody == null ? null : JSON.parseObject(requestBody, type);
                    } catch (Throwable t) {
                        return Render.text(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
        }
        try {
            return (Render) method.invoke(controller, parameterValues);
        } catch (Throwable t) {
            return Render.text(t.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    private Object doConvert(String value, Class<?> toType) {
        if (null == value) {
            return primitiveDefaults.get(toType);
        }
        Object result = null;
        if ((toType == Integer.class) || (toType == Integer.TYPE))
            result = Integer.parseInt(value);
        else if ((toType == Double.class) || (toType == Double.TYPE))
            result = Double.parseDouble(value);
        else if ((toType == Boolean.class) || (toType == Boolean.TYPE))
            result = Boolean.parseBoolean(value);
        else if ((toType == Byte.class) || (toType == Byte.TYPE))
            result = Byte.parseByte(value);
        else if ((toType == Character.class) || (toType == Character.TYPE))
            result = value.charAt(0);
        else if ((toType == Short.class) || (toType == Short.TYPE))
            result = Short.parseShort(value);
        else if ((toType == Long.class) || (toType == Long.TYPE))
            result = Long.parseLong(value);
        else if ((toType == Float.class) || (toType == Float.TYPE))
            result = Float.parseFloat(value);
        else if (toType == BigInteger.class)
            result = new BigInteger(value);
        else if (toType == BigDecimal.class)
            result = new BigDecimal(value);
        else if (toType == String.class)
            result = value;
        return result;
    }

    /**
     * 基本类型默认值
     */
    private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>(9);

    static {
        primitiveDefaults.put(boolean.class, false);
        primitiveDefaults.put(byte.class, (byte) 0);
        primitiveDefaults.put(short.class, (short) 0);
        primitiveDefaults.put(char.class, (char) 0);
        primitiveDefaults.put(int.class, 0);
        primitiveDefaults.put(long.class, 0L);
        primitiveDefaults.put(float.class, 0.0f);
        primitiveDefaults.put(double.class, 0.0);
    }
}
