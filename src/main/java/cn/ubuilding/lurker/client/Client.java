package cn.ubuilding.lurker.client;

import cn.ubuilding.lurker.support.NetUtils;
import cn.ubuilding.lurker.support.loadbalance.LoadBalance;
import cn.ubuilding.lurker.support.loadbalance.LoadBalanceFactory;
import cn.ubuilding.lurker.support.registry.Registry;
import cn.ubuilding.lurker.support.registry.ZookeeperRegistry;
import net.sf.cglib.proxy.Proxy;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/2 18:00
 * 目标服务的Proxy实现
 * 封装远程服务调用
 */

public final class Client {

    /**
     * remote服务地址
     */
    private volatile List<String> addresses;

    private String loadBalance;

    public Client(String registryAddress) {
        this(registryAddress, null);
    }

    /**
     * @param registryAddress 注册中心地址
     */
    public Client(String registryAddress, String loadBalance) {
        this(new ZookeeperRegistry(registryAddress), loadBalance);
    }

    public Client(Registry registry) {
        this(registry, null);
    }

    /**
     * @param registry    用于发现远程服务信息
     * @param loadBalance {@link LoadBalanceFactory} 中定义的名称
     */
    public Client(Registry registry, String loadBalance) {
        if (null == registry) {
            throw new IllegalArgumentException("registry must not be null");
        }

        registry.addListener(interfaceClass.getName(), this);

        List<String> addresses = registry.discover(interfaceClass.getName());
        if (null == addresses || addresses.size() == 0) {
            throw new IllegalStateException("not found any remote addresses for service(" + interfaceClass.getName() + ")");
        }
        this.addresses = addresses;
        this.loadBalance = loadBalance;
        setConnection();
    }

    /**
     * 返回一个目标服务接口的代理实例
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        if (null == interfaceClass) {
            throw new IllegalArgumentException("interface class must not be null");
        }
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RemoteServiceProxy(interfaceClass.getName()));
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * 设置远程连接对象
     * 从本地缓存的
     */
    private void setConnection() {
        if (null == this.addresses || this.addresses.size() == 0) {
            throw new IllegalStateException("not found valid remote address");
        }
        LoadBalance lb = (this.loadBalance == null || this.loadBalance.length() == 0) ?
                LoadBalanceFactory.getDefault() : LoadBalanceFactory.get(this.loadBalance);
        String address = (null == lb) ? this.addresses.get(0) : lb.select(this.addresses);
        String[] adds = address.split(NetUtils.ADDRESS_SEPARATOR);
        if (adds.length != 2) {
            throw new IllegalArgumentException("invalid remote address: " + address);
        }
        this.connection = new Connection(adds[0], Integer.parseInt(adds[1]));
    }


}
