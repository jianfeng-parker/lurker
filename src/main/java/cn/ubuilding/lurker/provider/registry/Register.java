package cn.ubuilding.lurker.provider.registry;

import cn.ubuilding.lurker.provider.ProviderInfo;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:51
 * <p>
 * 将Provider所表示的服务对外发布
 * 这里默认将其发布到zookeeper上
 */

public interface Register {

    void registry(String host, int port, List<ProviderInfo> providerInfos);
}
