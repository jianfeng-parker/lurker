package cn.ubuilding.lurker.provider.registry;

import cn.ubuilding.lurker.provider.ProviderInfo;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:54
 * 默认服务发布器
 * 注：这里的场景是将zookeeper作为默认的服务注册中心
 */

public class DefaultRegister implements Register {

    public void registry(String host, int port, List<ProviderInfo> providerInfos) {
        // TODO 将服务发布到注册中心
        if (null == providerInfos) return;
        for (ProviderInfo providerInfo : providerInfos) {
            System.out.println("Publish service:"+ providerInfo.getKey());
        }
    }
}
