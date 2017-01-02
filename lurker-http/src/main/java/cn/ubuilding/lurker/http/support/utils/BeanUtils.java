package cn.ubuilding.lurker.http.support.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Wu Jianfeng
 * @since 2017/1/2 11:48
 */

public final class BeanUtils {

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

    public static <T> T toBean(Map<String, String> map, Class<T> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        T obj = beanClass.newInstance();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            field.set(obj, getValue(map.get(field.getName()), field.getType()));
        }
        return obj;
    }

    public static Object defaultPrimitiveValue(Class<?> clazz) {
        return primitiveDefaults.get(clazz);
    }

    public static Object getValue(String value, Class<?> toType) {
        if (null == value) {
            return BeanUtils.defaultPrimitiveValue(toType);
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

    private BeanUtils() {
    }
}
