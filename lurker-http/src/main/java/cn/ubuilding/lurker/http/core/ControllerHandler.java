package cn.ubuilding.lurker.http.core;

import cn.ubuilding.lurker.http.annotation.RequestParam;
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

public class ControllerHandler {

    private Object controller;

    private Method method;

    public ControllerHandler(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Render handle(Map<String, String> parameters) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        ((RequestParam) annotations[1][0]).name();
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
                } else if (null != value && value.trim().length() > 0) {
                    try {
                        Object realValue = type.isInstance(value) ? value : doConvert(value, type);
                        parameterValues[i] = realValue;
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
            return new Render(t.getMessage(), RenderType.TEXT, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Render handle(Map<String, String> parameters, Map<String, List<String>> requestBody) {

        return null;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    private Object doConvert(String value, Class<?> toType) {
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
