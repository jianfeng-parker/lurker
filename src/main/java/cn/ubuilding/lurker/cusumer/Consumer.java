package cn.ubuilding.lurker.cusumer;

import cn.ubuilding.lurker.common.RemoteAddress;
import cn.ubuilding.lurker.common.Request;
import cn.ubuilding.lurker.common.Response;
import cn.ubuilding.lurker.cusumer.discover.DefaultDiscovery;
import cn.ubuilding.lurker.cusumer.discover.Discovery;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Wu Jianfeng
 * @since 16/4/2 18:00
 * // TODO 使用spring -> ProxyBeanFactory 生成目标接口实例
 */

public final class Consumer {

    private static AtomicLong callTimes = new AtomicLong(0L);

    private List<Connection> connections;

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
        RemoteAddress address = discovery.discover();
        if (null == address) {
            throw new RuntimeException("not discovered any remote service used " + discovery.description() + " by key(" + discovery.getKey() + ")");
        }
        this.serviceKey = discovery.getKey();
        initConnection(address.getHost(), address.getPort());
    }

    /**
     * 默认使用 {@link DefaultDiscovery} 发现远程服务
     *
     * @param key 远程服务唯一标识
     */
    public Consumer(String key) {
        this(new DefaultDiscovery(key));
        this.serviceKey = key;
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
                new LurkerInvoker(getConnection(), serviceKey));
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

    /**
     * 获取连接对象
     */
    private Connection getConnection() {
        int d = (int) (callTimes.getAndIncrement() % (connections.size() + 1));
        if (d == 0) {
            return connection;
        } else {
            return connections.get(d - 1);
        }
    }

    private void initConnection(String remoteHost, int remotePort) {
        this.connection = new Connection(remoteHost, remotePort);
        this.connection.connect();
        this.connections = new ArrayList<Connection>();
        int num = Runtime.getRuntime().availableProcessors() / 3 - 2;
        for (int i = 0; i < num; i++) {
            this.connections.add(new Connection(remoteHost, remotePort));
        }
        for (Connection conn : connections) {
            conn.connect();
        }
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
