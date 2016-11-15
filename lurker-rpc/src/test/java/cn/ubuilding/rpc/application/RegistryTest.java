package cn.ubuilding.rpc.application;

import cn.ubuilding.rpc.support.registry.Registry;
import cn.ubuilding.rpc.support.registry.ZookeeperRegistry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 2016/10/15 22:16
 */

public class RegistryTest {
    private static Registry registry;

    @BeforeClass
    public static void setup() {
        registry = new ZookeeperRegistry("127.0.0.1:2181");
    }

    @Test
    public void testDiscover() {
        List<String> addresses = registry.discover(null);
        Assert.assertNotNull(addresses);
    }
}
