package cn.ubuilding.lurker.v2.rpc.api;

import cn.ubuilding.lurker.v2.common.URL;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Wu Jianfeng
 * @since 16/8/10 18:12
 */

public abstract class AbstractProxyInvoker<T> implements Invoker<T> {

    private Class<T> interfaceClass;

    private T proxy;

    private URL url;

    public Class<T> getInterface() {
        return interfaceClass;
    }

    public AbstractProxyInvoker(Class<T> interfaceClass, T proxy, URL url) {
        if (null == proxy) {
            throw new IllegalArgumentException("proxy is null");
        }
        if (null == interfaceClass) {
            throw new IllegalArgumentException("interface is null");
        }
        if (!interfaceClass.isInstance(proxy)) {
            throw new IllegalArgumentException(proxy.getClass().getName() + " not implement " + interfaceClass);
        }
        this.interfaceClass = interfaceClass;
        this.proxy = proxy;
        this.url = url;
    }

    public Result invoke(Invocation invocation) {
        try {
            return new RpcResult(doInvoke(proxy, invocation.getMethod(), invocation.getParameterTypes(), invocation.getArguments()));
        } catch (InvocationTargetException e) {
            return new RpcResult(e.getTargetException());
        } catch (Throwable t) {
            throw new RpcException("failed to invoke remote proxy method:" + invocation.getMethod() + " to, because: " + t.getMessage(), t);
        }
    }

    protected abstract Object doInvoke(T proxy, String method, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;

    public URL getUrl() {
        return url;
    }

    public void destroy() {

    }

    public String toString() {
        return getInterface() + "->" + (getUrl() == null ? "" : getUrl().toString());
    }
}
