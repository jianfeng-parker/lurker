package cn.ubuilding.lurker.registry.publish;

import cn.ubuilding.lurker.provider.Provider;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:54
 * 默认服务发布器
 * 注：这里的场景是将zookeeper作为默认的服务注册中心
 */

public class DefaultPublisher implements Publisher {

    public void publish(String host, int port, List<Provider> providers) {
        // TODO 将服务发布到注册中心
        if (null == providers) return;
        for (Provider provider : providers) {
            System.out.println("Publish service:"+ provider.getKey());
        }
    }
}
