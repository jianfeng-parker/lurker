package cn.ubuilding.lurker.extension;

import cn.ubuilding.lurker.v2.common.extention.ExtensionLoader;
import cn.ubuilding.lurker.extension.service.TestService;
import cn.ubuilding.lurker.v2.rpc.ProxyFactory;
import cn.ubuilding.lurker.v2.rpc.proxy.cglib.CglibProxyFactory;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author Wu Jianfeng
 * @since 16/8/9 21:51
 */

public class ExtensionLoaderTest extends TestCase {

    public void testLoad() {
        ProxyFactory proxyFactory1 = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getDefaultInstance();
        Assert.assertNotNull(proxyFactory1);
        Assert.assertEquals(proxyFactory1.getClass(), CglibProxyFactory.class);
        ProxyFactory proxyFactory2 = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getDefaultInstance();
        Assert.assertEquals(proxyFactory1, proxyFactory2);// 两次获取的是同一个实例
    }

    public void testDuplicate() {
        TestService service = ExtensionLoader.getExtensionLoader(TestService.class).getDefaultInstance();

    }

}
