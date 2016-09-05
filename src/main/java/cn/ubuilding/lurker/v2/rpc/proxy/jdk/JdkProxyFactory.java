package cn.ubuilding.lurker.v2.rpc.proxy.jdk;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.rpc.api.Invoker;
import cn.ubuilding.lurker.v2.rpc.ProxyFactory;

/**
 * @author Wu Jianfeng
 * @since 16/8/8 07:27
 */

public class JdkProxyFactory implements ProxyFactory {
    public <T> T getProxy(Invoker<T> invoker) {
        return null;
    }

    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return null;
    }
}
