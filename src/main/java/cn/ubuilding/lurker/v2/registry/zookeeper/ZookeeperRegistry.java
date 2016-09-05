package cn.ubuilding.lurker.v2.registry.zookeeper;

import cn.ubuilding.lurker.v2.common.Constants;
import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.registry.Registry;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;


/**
 * @author Wu Jianfeng
 * @since 16/7/13 16:02
 */

public class ZookeeperRegistry implements Registry {

    private static final String DEFAULT_ROOT = "lurker";

    private final ZkClient client;

    public ZookeeperRegistry(URL url) {
        client = new ZkClient(url.getAddress());
    }

    public void register(URL url) {
        try {
            client.createPersistent(toServicePath(url), true);
        } catch (ZkNodeExistsException ignored) {
        }
    }

    public void unRegister(URL url) {
        try {
            client.delete(toServicePath(url));
        } catch (ZkNoNodeException ignored) {
        }
    }

    public void subscribe(URL url) {

    }

    public void unSubscribe(URL url) {

    }

    public void destroy() {
        client.close();
    }

    private String toServicePath(URL url) {
        return DEFAULT_ROOT + Constants.PATH_SEPARATOR + URL.encode(url.getPath()) + Constants.PATH_SEPARATOR + url.toString();
    }
}
