package cn.ubuilding.lurker.consumer;

import cn.ubuilding.lurker.protocol.Request;
import cn.ubuilding.lurker.protocol.Response;
import cn.ubuilding.lurker.consumer.discover.DefaultDiscovery;
import cn.ubuilding.lurker.consumer.discover.Discovery;
import cn.ubuilding.lurker.util.HostAndPort;
import cn.ubuilding.lurker.event.LurkerListener;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author Wu Jianfeng
 * @since 16/4/2 18:00
 * 目标服务的Proxy实现
 * 封装远程服务调用
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

        // 在Discovery中设置一个监听远程服务地址变化的监听器
        discovery.setChanger(new RemoteAddressListener());

        HostAndPort address = discovery.discover();
        if (null == address) {
            throw new RuntimeException("not discovered any remote service used " + discovery.description() + " by serviceKey(" + discovery.getServiceKey() + ")");
        }
        this.serviceKey = discovery.getServiceKey();
        this.interfaceClass = interfaceClass;
        this.connection = new Connection(address.getHost(), address.getPort());
    }

    /**
     * 返回一个目标服务接口的代理实例
     */
    @SuppressWarnings("unchecked")
    public <T> T instance() {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RemoteServiceInvoker(this.serviceKey));
    }

    public String remoteHost() {
        return connection.getHost();
    }

    public int remotePort() {
        return connection.getPort();
    }

    public boolean isConnected() {
        return connection.isConnected();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private class RemoteServiceInvoker implements InvocationHandler {

        private String serviceKey;

        public RemoteServiceInvoker(String serviceKey) {
            this.serviceKey = serviceKey;
        }

        public Response invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Request request = new Request();
            request.setId(UUID.randomUUID().toString());
            request.setServiceKey(serviceKey);
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            Connection connection = Consumer.this.getConnection();
            if (null == connection) throw new RuntimeException("not connect to remote service for " + serviceKey);

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

    private class RemoteAddressListener implements LurkerListener<HostAndPort> {
        /**
         * 远程服务地址变更后，重新设置该Consumer对象中的connection
         *
         * @param address 变更后的远程服务地址
         */
        public void onChange(HostAndPort address) {
            if (null != address) {
                Consumer.this.getConnection().close();
                Consumer.this.setConnection(new Connection(address.getHost(), address.getPort()));
            }
        }
    }
}
