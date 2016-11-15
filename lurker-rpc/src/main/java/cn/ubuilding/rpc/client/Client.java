package cn.ubuilding.rpc.client;

import cn.ubuilding.rpc.client.proxy.AsyncRemoteProxy;
import cn.ubuilding.rpc.client.proxy.RemoteProxyInvocation;
import cn.ubuilding.rpc.support.registry.RegistryChanger;
import cn.ubuilding.rpc.support.loadbalance.LoadBalanceFactory;
import cn.ubuilding.rpc.support.registry.Registry;
import cn.ubuilding.rpc.support.registry.ZookeeperRegistry;
import net.sf.cglib.proxy.Proxy;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Wu Jianfeng
 * @since 16/4/2 18:00
 * 目标服务的Proxy实现
 * 封装远程服务调用
 */

public final class Client {

    /**
     * 缓存remote服务地址
     */
    private static ConcurrentHashMap<String, List<String>> addresses = new ConcurrentHashMap<String, List<String>>();

    // TODO 线程池初始化计算公式
    private static ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            600L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private Registry registry;

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
        this.registry = registry;
        this.loadBalance = loadBalance;
    }

    /**
     * 返回一个目标服务接口的代理实例
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, buildProxyInvocation(interfaceClass));
    }

    public <T> AsyncRemoteProxy createAsync(Class<T> interfaceClass) {
        return buildProxyInvocation(interfaceClass);
    }

    public void stop() {
        executor.shutdown();
        registry.close();
    }

    private <T> RemoteProxyInvocation buildProxyInvocation(Class<T> interfaceClass) {
        if (null == interfaceClass) {
            throw new IllegalArgumentException("interface class must not be null");
        }
        List<String> addrList = addresses.get(interfaceClass.getName());
        if (null == addrList || addrList.size() == 0) {
            List<String> list = registry.discover(interfaceClass.getName());
            if (null == list || list.size() == 0) {
                throw new IllegalStateException("not found any remote service addresses");
            } else {
                addresses.putIfAbsent(interfaceClass.getName(), list);
                addrList = addresses.get(interfaceClass.getName());
            }
        }
        // invocation内部持有远程连接，并实现远程通信
        // 将invocation赋予Changer实例是便于 Changer监听到Registry发生变化后可以更新invocation内部连接等信息
        RemoteProxyInvocation invocation = new RemoteProxyInvocation(interfaceClass.getName(), addrList, loadBalance);
        registry.addListener(interfaceClass.getName(), new RemoteAddressChanger(invocation));
        return invocation;
    }

    public static void submit(Runnable command) {
        if (null != command) {
            executor.submit(command);
        }
    }

    private class RemoteAddressChanger implements RegistryChanger<String, List<String>> {

        private RemoteProxyInvocation invocation;

        public RemoteAddressChanger(RemoteProxyInvocation invocation) {
            this.invocation = invocation;
        }

        /**
         * 远程服务地址变更后，重新设置该Consumer对象中的connection
         *
         * @param addressList 变更后的远程服务地址
         */
        public void onChange(String serviceName, List<String> addressList) {
            if (null != addressList && addressList.size() > 0) {
                addresses.putIfAbsent(serviceName, addressList);
                invocation.updateConnection(addressList);
            }
        }
    }

}
