package cn.ubuilding.lurker.cusumer.discover;

import java.net.InetSocketAddress;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 21:38
 * 用于从Redis上获取服务提供者信息
 */

public class RedisDiscovery extends Discovery {

    public RedisDiscovery(String key) {
        this.key = key;
    }

    public InetSocketAddress discover() {
        return null;
    }

    public String description() {
        return "Redis Discovery";
    }
}
