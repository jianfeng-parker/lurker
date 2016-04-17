package cn.ubuilding.lurker.consumer.discover;

import cn.ubuilding.lurker.util.HostAndPort;
import cn.ubuilding.lurker.event.LurkerListener;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 21:38
 * 用于从Redis上获取服务提供者信息
 */

public class RedisDiscovery extends Discovery {

    public RedisDiscovery(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public HostAndPort discover() {
        return null;
    }

    public void stop() {

    }

    public String description() {
        return "Redis Discovery";
    }

    public void setChanger(LurkerListener<HostAndPort> changer) {

    }
}
