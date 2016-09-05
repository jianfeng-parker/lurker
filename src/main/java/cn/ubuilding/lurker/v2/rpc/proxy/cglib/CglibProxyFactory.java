package cn.ubuilding.lurker.v2.rpc.proxy.cglib;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.common.extention.Default;
import cn.ubuilding.lurker.v2.rpc.api.AbstractProxyInvoker;
import cn.ubuilding.lurker.v2.rpc.api.Invoker;
import cn.ubuilding.lurker.v2.rpc.ProxyFactory;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

/**
 * @author Wu Jianfeng
 * @since 16/8/8 07:28
 */

@Default
public class CglibProxyFactory implements ProxyFactory {

    public <T> T getProxy(Invoker<T> invoker) {
        return null;
    }

    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new AbstractProxyInvoker<T>(type, proxy, url) {
            @Override
            protected Object doInvoke(T proxy, String method, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
                Class<?> serviceClass = proxy.getClass();
                FastClass fastClass = FastClass.create(serviceClass);
                FastMethod fastMethod = fastClass.getMethod(method, parameterTypes);
                return fastMethod.invoke(proxy, arguments);
            }
        };
    }
}
