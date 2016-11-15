package cn.ubuilding.lurker.rpc.support.registry;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/10/14 08:32
 */

public class RedisRegistry implements Registry {

    public void register(String serviceName, String serviceAddress) {

    }

    public List<String> discover(String serviceName) {
        return null;
    }

    public void unRegister(String serviceName, String serviceAddress) {

    }

    public void addListener(String serviceName, RegistryChanger listener) {

    }

    public void check() {
        // TODO 做一些注册中心相关的校验
    }

    public void close() {

    }
}
