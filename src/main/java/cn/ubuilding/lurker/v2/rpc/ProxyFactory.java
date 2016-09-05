package cn.ubuilding.lurker.v2.rpc;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.rpc.api.Invoker;

/**
 * @author Wu Jianfeng
 * @since 16/8/8 10:41
 */

public interface ProxyFactory {

    <T> T getProxy(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url);
}
