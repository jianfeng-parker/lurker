package cn.ubuilding.lurker.cusumer;

import cn.ubuilding.lurker.protocol.Request;
import cn.ubuilding.lurker.protocol.Response;
import cn.ubuilding.lurker.registry.discover.DefaultDiscovery;
import cn.ubuilding.lurker.registry.discover.Discovery;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
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

    private Class<?> interfaceClass;

    /**
     * 默认使用 {@link DefaultDiscovery} 发现远程服务
     *
     * @param serviceKey      远程服务唯一标识
     * @param registryAddress 服务注册中心地址
     * @param interfaceClass  目标服务接口的class
     */
    public Consumer(String serviceKey, String registryAddress, Class<?> interfaceClass) {
        this(new DefaultDiscovery(serviceKey, registryAddress), interfaceClass);
    }

    /**
     * @param discovery 用于发现远程服务信息
     */
    public Consumer(Discovery discovery, Class<?> interfaceClass) {
        if (null == discovery) {
            throw new IllegalArgumentException("discovery must not be null");
        }
        if (null == interfaceClass) {
            throw new IllegalArgumentException("interface class must not be null");
        }
        String address = discovery.discover();
        if (null == address) {
            throw new RuntimeException("not discovered any remote service used " + discovery.description() + " by serviceKey(" + discovery.getServiceKey() + ")");
        }
        this.serviceKey = discovery.getServiceKey();
        this.interfaceClass = interfaceClass;
        this.connection = ConnectionFactory.getConnection(address);
    }

    /**
     * 返回一个目标服务接口的代理实例
     */
    @SuppressWarnings("unchecked")
    public <T> T instance() {
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

        public Response invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Request request = new Request();
            request.setId(UUID.randomUUID().toString());
            request.setServiceKey(serviceKey);
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            Response response = connection.send(request);
            if (null != response) {
                if (null != response.getError()) {
                    throw response.getError();
                }
                return response;
            } else {
                return null;
            }
        }
    }
}
