package cn.ubuilding.lurker.registry.discover;

import cn.ubuilding.lurker.registry.HostAndPort;
import cn.ubuilding.lurker.registry.event.LurkerListener;
import cn.ubuilding.lurker.registry.Constant;
import org.apache.zookeeper.KeeperException;
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

public class ZooKeeperDiscovery extends Discovery {

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
    private HostAndPort remoteAddress;

    /**
     * 相当于一个Listener
     */
    private LurkerListener<HostAndPort> changer;

    public ZooKeeperDiscovery(String serviceKey, String registryAddress) {
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

    public HostAndPort discover() {
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
            byte[] data = zk.getData(Constant.fullPathForZk(serviceKey), new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeDataChanged) {
                        getData(zk);
                    }
                }
            }, null);

            if (null != data) {
                remoteAddress = new HostAndPort(new String(data, "UTF-8"));// 更新本地属性值
                if (null != changer) changer.onChange(remoteAddress);
            }
        } catch (KeeperException.NoNodeException e) {
            System.out.println(">>>>" + e.getMessage());
        } catch (Exception e) {
            // TODO logging...
            System.out.println(">>>>get data failure:" + e.getMessage());

        }
    }

    public String description() {
        return "Default Discovery(zookeeper)";
    }

    public void setChanger(LurkerListener<HostAndPort> changer) {
        this.changer = changer;
    }

}
