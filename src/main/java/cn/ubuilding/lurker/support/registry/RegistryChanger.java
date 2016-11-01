package cn.ubuilding.lurker.support.registry;

/**
 * @author Wu Jianfeng
 * @since 16/4/13 22:18
 */

public interface RegistryChanger<N, T> {

    void onChange(N serviceName, T event);
}
