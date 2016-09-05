package cn.ubuilding.lurker.v2.registry;

import cn.ubuilding.lurker.v2.common.URL;

/**
 * @author Wu Jianfeng
 * @since 16/7/14 17:44
 */

public interface Registry {

    /**
     * 注册数据
     *
     * @param url 携带被注册数据，即注册中心信息
     */
    void register(URL url);

    /**
     * 取消注册
     */
    void unRegister(URL url);

    /**
     * 向注册中心订阅数据
     */
    void subscribe(URL url);

    /**
     * 取消订阅
     */
    void unSubscribe(URL url);

    void destroy();
}
