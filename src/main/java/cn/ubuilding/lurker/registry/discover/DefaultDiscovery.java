package cn.ubuilding.lurker.registry.discover;

import cn.ubuilding.lurker.registry.Constant;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 21:35
 * 用于获取服务提供者信息的默认实现类
 * 注: 默认从zookeeper上获取信息
 */

public class DefaultDiscovery extends Discovery {

    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * 注册中心地址(zookeeper集群)，格式:192.168.1.10:2181,192.168.1.11:2181
     */
    private String registryAddress;

    /**
     * ZK 客户端
     */
    private ZooKeeper zooKeeper;

    /**
     * 从注册中心获取的远程服务地址
     */
    private String remoteAddress;

    public DefaultDiscovery(String serviceKey, String registryAddress) {
        if (null == serviceKey || serviceKey.length() == 0) {
            throw new NullPointerException("serviceKey");
        }
        if (null == registryAddress || registryAddress.length() == 0) {
            throw new NullPointerException("registryAddress");
        }
        this.serviceKey = serviceKey;
        this.registryAddress = registryAddress;
        this.zooKeeper = connect();
        if (null != zooKeeper) {
            getData(zooKeeper);
        }
    }

    public String discover() {
        if (null == remoteAddress) getData(zooKeeper);
        return remoteAddress;
    }

    public void stop() {
        if (null != zooKeeper) {
            try {
                zooKeeper.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取zk客户端连接
     */
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
            // TODO logging...
        }
        return zk;
    }

    /**
     * 获取serviceKey节点数据(该key所指向的远程服务地址信息),
     * 并设置对该节点的监听
     */
    private void getData(final ZooKeeper zk) {
        try {
            // TODO 节点路径
            byte[] data = zk.getData("", new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeDataChanged) {
                        getData(zk);
                        // TODO 节点(远程服务地址)变化后更新客户端信息
                    }
                }
            }, null);

            if (null != data) remoteAddress = new String(data, "UTF-8");
        } catch (Exception e) {
            // TODO logging...

        }
    }

    public String description() {
        return "Default Discovery(zookeeper)";
    }

}
