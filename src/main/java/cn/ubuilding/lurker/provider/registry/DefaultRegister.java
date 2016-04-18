package cn.ubuilding.lurker.provider.registry;

import cn.ubuilding.lurker.provider.Provider;
import cn.ubuilding.lurker.util.Constant;
import cn.ubuilding.lurker.util.HostAndPort;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:54
 * 默认服务发布器
 * 注：这里的场景是将zookeeper作为默认的服务注册中心
 */

public class DefaultRegister extends Register {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRegister.class);

    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * zookeeper(注册中心) 地址，格式:
     * "host1:port1,host2:port2"
     */
    private String registryAddress;

    private ZooKeeper zookeeper;

    public DefaultRegister(String host, int port, List<Provider> providers, String registryAddress) {
        if (null == host || host.length() == 0) {
            throw new IllegalArgumentException("host must not be null");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("invalid port:" + port);
        }
        if (null == providers || providers.size() == 0) {
            throw new IllegalArgumentException("no providers to be published");
        }
        if (null == registryAddress || registryAddress.length() == 0) {
            throw new IllegalArgumentException("registry address must not be null");
        }
        this.rpcAddress = new HostAndPort(host, port);
        this.providers = providers;
        this.registryAddress = registryAddress;
    }

    public void publish() {
        if (null == zookeeper || !zookeeper.getState().isAlive()) {
            zookeeper = connect();
        }
        boolean existRootPath = exist(Constant.ZK_REGISTRY_ROOT_PATH);
        if (!existRootPath) {// 如果根节点不存在创建一个根节点
            create(Constant.ZK_REGISTRY_ROOT_PATH, new byte[0]);
        }
        for (Provider provider : getProviders()) {
            // serviceKey对应zookeeper 中的path
            String servicePath = Constant.fullPathForZk(provider.getServiceKey());
            if (exist(servicePath)) {  // 如果已注册，则更新
                update(servicePath, getRpcAddress().toString().getBytes());
            } else {
                create(servicePath, getRpcAddress().toString().getBytes());
            }
        }
        // 发布完成后，关闭连接
        close();
    }

    private void close() {
        try {
            zookeeper.close();
        } catch (Exception e) {
            logger.error("close connection to zookeeper(" + registryAddress + ") error:" + e.getMessage());
        }
    }

    private ZooKeeper connect() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_CONNECTION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            logger.error("connect to zookeeper(" + registryAddress + ") failure:" + e.getMessage());
        }
        return zk;
    }

    private void create(String path, byte[] data) {
        try {
            zookeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {
            logger.error("create node(" + path + ") in zookeeper(" + registryAddress + ") failure:" + e.getMessage());
        }
    }

    private void update(String path, byte[] data) {
        try {
            zookeeper.setData(path, data, -1);
        } catch (Exception e) {
            logger.error("update node(" + path + ") in zookeeper(" + registryAddress + ") failure:" + e.getMessage());

        }
    }

    private boolean exist(String path) {
        try {
            Stat stat = zookeeper.exists(path, false);
            return null != stat;
        } catch (Exception e) {
            logger.error("find node(" + path + ") to judge exist or not in zookeeper(" + registryAddress + ") failure:" + e.getMessage());
            return false;
        }
    }

}
