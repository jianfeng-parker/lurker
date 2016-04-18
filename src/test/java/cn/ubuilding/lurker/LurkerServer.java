package cn.ubuilding.lurker;

//import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.ubuilding.lurker.application.provider.impl.DoSomethingServiceImpl;
import cn.ubuilding.lurker.application.provider.impl.HelloServiceImpl;
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
        provider1.setServiceKey("helloService_1.0");
        provider1.setImplementation(new HelloServiceImpl());

        providers.add(provider1);

        Provider provider2 = new Provider();
        provider2.setServiceKey("doSomethingService_1.0");
        provider2.setImplementation(new DoSomethingServiceImpl());

        providers.add(provider2);

        new Server("127.0.0.1", 8899, providers, "127.0.0.1:2181").start();
    }
}
