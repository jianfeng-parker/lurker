package cn.ubuilding.lurker.cusumer;

import cn.ubuilding.lurker.common.Request;
import cn.ubuilding.lurker.common.Response;
import cn.ubuilding.lurker.cusumer.discover.DefaultDiscovery;
import cn.ubuilding.lurker.cusumer.discover.Discovery;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author Wu Jianfeng
 * @since 16/4/2 18:00
 * 目标服务的Proxy实现
 * // TODO 使用spring -> ProxyBeanFactory 生成目标接口实例
 */

public final class Consumer {

    private Connection connection;

    /**
     * 远程服务唯一标识
     */
    private String serviceKey;

    /**
     * @param discovery 用于发现远程服务信息
     */
    public Consumer(Discovery discovery) {
        if (null == discovery) {
            throw new IllegalArgumentException("discovery must not be null");
        }
        InetSocketAddress address = discovery.discover();
        if (null == address) {
            throw new RuntimeException("not discovered any remote service used " + discovery.description() + " by key(" + discovery.getKey() + ")");
        }
        this.serviceKey = discovery.getKey();
        this.connection = ConnectionFactory.getConnection(address);
    }

    /**
     * 默认使用 {@link DefaultDiscovery} 发现远程服务
     *
     * @param key 远程服务唯一标识
     */
    public Consumer(String key) {
        this(new DefaultDiscovery(key));
    }

    /**
     * 返回一个目标服务接口的代理实例
     *
     * @param interfaceClass 远程服务接口 class
     */
    @SuppressWarnings("unchecked")
    public <T> T instance(Class<T> interfaceClass) {
        if (null == interfaceClass) throw new IllegalArgumentException("interface class must not be null");

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new LurkerInvoker(this.connection, this.serviceKey));
    }

    public String getRemoteHost() {
        return connection.getHost();
    }

    public int getRemotePort() {
        return connection.getPort();
    }

    public String getServiceKey() {
        return serviceKey;
    }

    private class LurkerInvoker implements InvocationHandler {

        private Connection connection;

        private String serviceKey;

        public LurkerInvoker(Connection connection, String serviceKey) {
            this.connection = connection;
            this.serviceKey = serviceKey;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Request request = new Request();
            request.setId(UUID.randomUUID().toString());
            request.setServiceKey(serviceKey);
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            Response response = connection.send(request);

            if (null != response.getError()) {
                throw response.getError();
            }
            return response;
        }
    }
}
