package cn.ubuilding.lurker.application.consumer;

import cn.ubuilding.lurker.util.HostAndPort;
import cn.ubuilding.lurker.consumer.discover.DefaultDiscovery;
import cn.ubuilding.lurker.consumer.discover.Discovery;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Wu Jianfeng
 * @since 16/4/13 17:03
 */

public class DefaultDiscoveryTest {

    private static Discovery discovery;

    @BeforeClass
    public static void setup() {
        discovery = new DefaultDiscovery("helloservice.1.0", "127.0.0.1:2181");
    }

    @Test
    public void testDiscover() {
        HostAndPort address = discovery.discover();
        Assert.assertNotNull(address);
    }

}
