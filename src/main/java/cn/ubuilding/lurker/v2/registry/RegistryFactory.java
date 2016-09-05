package cn.ubuilding.lurker.v2.registry;

import cn.ubuilding.lurker.v2.common.URL;

/**
 * @author Wu Jianfeng
 * @since 16/8/8 18:20
 */

public interface RegistryFactory {

    /**
     * 获取注册中心实例
     *
     * @param url 注册中心地址及其它属性的封装
     * @return 注册中心实例
     */
    Registry getRegistry(URL url);
}
