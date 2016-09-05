package cn.ubuilding.lurker.v2.registry.zookeeper;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.common.extention.Default;
import cn.ubuilding.lurker.v2.registry.AbstractRegistryFactory;
import cn.ubuilding.lurker.v2.registry.Registry;

/**
 * @author Wu Jianfeng
 * @since 16/8/21 11:22
 */
@Default
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    public Registry createRegistry(URL url) {
        return new ZookeeperRegistry(url);
    }
}
