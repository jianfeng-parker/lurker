package cn.ubuilding.lurker.v2.registry;

import cn.ubuilding.lurker.v2.common.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Wu Jianfeng
 * @since 16/8/21 11:25
 */

public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRegistryFactory.class);

    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * 注册中心集合
     */
    private static final Map<String, Registry> registries = new ConcurrentHashMap<String, Registry>();

    /**
     * 获取所有注册中心实例
     */
    public static Collection<Registry> getRegistries() {
        return Collections.unmodifiableCollection(registries.values());
    }

    /**
     * 关闭所有已创建的注册中心
     */
    public static void destroyAll() {
        lock.lock();
        try {
            for (Registry registry : getRegistries()) {
                try {
                    registry.destroy();
                } catch (Throwable t) {
                    logger.error("destroy registry error:" + t.getMessage(), t);
                }
            }
            registries.clear();
        } finally {
            lock.unlock();
        }
    }

    public Registry getRegistry(URL url) {
        String key = url.toServiceString();
        lock.lock();
        try {
            Registry registry = registries.get(key);
            if (null != registry) {
                return registry;
            }
            registry = createRegistry(url);
            if (null == registry) {
                throw new IllegalStateException("could not create registry by url:" + url);
            }
            registries.put(key, registry);
            return registry;
        } finally {
            lock.unlock();
        }
    }

    protected abstract Registry createRegistry(URL url);
}
