package cn.ubuilding.lurker.v2.common.extention;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Wu Jianfeng
 * @since 16/8/8 20:51
 */

public class ExtensionLoader<T> {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> ExtensionLoaders = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();

    // class的默认实现类实例缓存
    private static final ConcurrentMap<Class<?>, Object> CachedInstances = new ConcurrentHashMap<Class<?>, Object>();

    private Class<?> clazz;

//    private ExtensionFactory objectFactory;

    /**
     * 获取接口的默认的实现类
     *
     * @param clazz 接口类
     * @return 默认实现类
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz) {
        if (clazz == null || !clazz.isInterface()) {
            throw new IllegalArgumentException("invalid interface:" + clazz + " to get default implementation");
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>) ExtensionLoaders.get(clazz);
        if (loader == null) {
            ExtensionLoaders.putIfAbsent(clazz, new ExtensionLoader<T>(clazz));
            loader = (ExtensionLoader<T>) ExtensionLoaders.get(clazz);
        }
        return loader;
    }

    /**
     * 获取接口的默认实现类的实例
     *
     * @return instance of default implementation
     */
    @SuppressWarnings("unchecked")
    public T getDefaultInstance() {
        T instance = (T) CachedInstances.get(clazz);
        if (null == instance) {
            synchronized (CachedInstances) {
                instance = (T) CachedInstances.get(clazz);
                if (null == instance) {
                    ServiceLoader<T> loaders = (ServiceLoader<T>) ServiceLoader.load(clazz);
                    for (T loader : loaders) {
                        Annotation annotation = loader.getClass().getAnnotation(Default.class);
                        if (clazz.isInstance(loader) && null != annotation) {
                            if (null != instance) {
                                throw new IllegalStateException("duplicate default implementation of " + clazz);
                            }
                            instance = loader;
                        }
                    }
                    if (instance != null) {
                        injectInstance(instance);
                        CachedInstances.putIfAbsent(clazz, instance);
                    }
                }
            }
        }
        return instance;
    }

    private ExtensionLoader(Class<?> clazz) {
        this.clazz = clazz;
//        objectFactory = clazz == ExtensionFactory.class ? null : ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getDefaultInstance();
    }

    private void injectInstance(T instance) {
        for (Method method : instance.getClass().getMethods()) {
            if (method.getName().startsWith("set") && method.getParameterTypes().length == 1
                    && Modifier.isPublic(method.getModifiers())) {
                Class<?> parameterType = method.getParameterTypes()[0];
                try {
                    // 这里直接使用ExtensionLoader扩展机制获取注入instance的属性值
                    // TODO 也可以通过Factory扩展获取instance属性值的多种方式
                    Object object = ExtensionLoader.getExtensionLoader(parameterType).getDefaultInstance();
                    if (null != object) {
                        method.invoke(instance, object);
                    }
                } catch (Throwable t) {
                    logger.error("inject property of type(" + parameterType + ") into instance(" + instance + ") error:" + t.getMessage(), t);
                }
            }
        }
    }
}
