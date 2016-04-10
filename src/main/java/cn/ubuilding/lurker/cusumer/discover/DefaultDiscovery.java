package cn.ubuilding.lurker.cusumer.discover;

import java.net.InetSocketAddress;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 21:35
 * 用于获取服务提供者信息的默认实现类
 * 注: 默认从zookeeper上获取信息
 */

public class DefaultDiscovery extends Discovery {

    public DefaultDiscovery(String key) {
        this.key = key;
    }

    public InetSocketAddress discover() {

        return null;
    }

    public String description() {
        return "Default Discovery(zookeeper)";
    }

}
