package cn.ubuilding.lurker.support.registry;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:54
 * 默认服务发布器
 * 注：这里的场景是将zookeeper作为默认的服务注册中心
 */

public class ZookeeperRegistry implements Registry {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);

    private ZkClient zkClient;

    /**
     * zookeeper(注册中心) 地址，格式:
     * "host1:port1,host2:port2"
     */
    public ZookeeperRegistry(String zkAddress) {
        if (null == zkAddress || zkAddress.length() == 0) {
            throw new IllegalArgumentException("registry address is invalid");
        }
        this.zkClient = new ZkClient(zkAddress);
    }

    /**
     * /lurker
     * ....../serviceName1
     * ................../address1
     * ................../address2
     * ................../address3
     * ....../serviceName2
     * ................../address1
     * ................../address2
     */
    public void register(String serviceName, String serviceAddress) {
        String servicePath = completePath(serviceName);
        if (!exist(servicePath)) {
            zkClient.createPersistent(servicePath, true);
        }
        String path = completePath(serviceName + "/" + serviceAddress);
        if (!exist(path)) {
            zkClient.createEphemeral(path);
        }
    }

    public void unRegister(String serviceName, String serviceAddress) {
        zkClient.delete(completePath(serviceName + "/" + serviceAddress));
    }

    public List<String> discover(String serviceName) {
        String path = completePath(serviceName);
        return zkClient.getChildren(path);
    }

    /**
     * zookeeper节点发生变化时触发
     */
    @SuppressWarnings("unchecked")
    public void addListener(final String serviceName, final RegistryChanger changer) {
        zkClient.subscribeChildChanges(completePath(serviceName), new IZkChildListener() {
            public void handleChildChange(String servicePath, List<String> addressList) throws Exception {
                changer.onChange(serviceName, addressList);
            }
        });
    }

    public void check() {
        // TODO 做一些校验，如zookeeper地址格式...
    }

    private boolean exist(String path) {
        try {
            return zkClient.exists(path);
        } catch (Exception e) {
            logger.error("find node(" + path + ") to judge exist or not failure:" + e.getMessage());
            return false;
        }
    }

    private String completePath(String childPath) {
        return "/lurker/" + childPath;
    }

//    public static void main(String[] args) {
//        ZookeeperRegistry registry = new ZookeeperRegistry("localhost:2181");
//        registry.addListener("helloService_1.0", new LurkerChanger<String, List<String>>() {
//            public void onChange(String serviceName, List<String> event) {
//                System.out.println(">>>>>" + event);
//            }
//        });
//
//        while (true) {
//
//        }
//    }

}
