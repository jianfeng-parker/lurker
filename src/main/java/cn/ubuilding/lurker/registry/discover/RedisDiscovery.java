package cn.ubuilding.lurker.registry.discover;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 21:38
 * 用于从Redis上获取服务提供者信息
 */

public class RedisDiscovery extends Discovery {

    public RedisDiscovery(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String discover() {
        return null;
    }

    public void stop() {

    }

    public String description() {
        return "Redis Discovery";
    }
}
