package cn.ubuilding.lurker.v2.rpc.api;

import cn.ubuilding.lurker.v2.common.URL;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 07:23
 */

public interface Invoker<T> {

    Class<T> getInterface();

    Result invoke(Invocation invocation);

    URL getUrl();

    void destroy();
}
