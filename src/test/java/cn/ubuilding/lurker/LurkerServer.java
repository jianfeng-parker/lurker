package cn.ubuilding.lurker;

//import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.ubuilding.lurker.provider.Provider;
import cn.ubuilding.lurker.provider.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/16 15:20
 */

public class LurkerServer {

    public static void main(String[] args) {
        // spring 方式
//        new ClassPathXmlApplicationContext("classpath:applicationContext-provider.xml").start();

        // java代码 方式
        List<Provider> providers = new ArrayList<Provider>();

        Provider provider1 = new Provider();
        provider1.setServiceKey("xxService");
//        provider1.setImplementation(); // TODO set 服务接口实现类

        providers.add(provider1);

        Provider provider2 = new Provider();
        provider2.setServiceKey("xxService");
//        provider2.setImplementation(); // TODO set 服务接口实现类

        providers.add(provider2);

        new Server("127.0.0.1", 8899, providers, "127.0.0.1:2181").start();
    }
}
