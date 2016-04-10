package cn.ubuilding.lurker.provider.registry;

import cn.ubuilding.lurker.provider.ProviderInfo;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:56
 * 将服务发布到Redis上
 * 注：这里的场景是将Redis作为作为服务注册中心
 */

public class RedisRegister implements Register {
    public void registry(String host, int port, List<ProviderInfo> providerInfos) {

    }
}
