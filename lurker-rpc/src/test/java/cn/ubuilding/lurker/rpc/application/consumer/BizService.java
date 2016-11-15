package cn.ubuilding.lurker.rpc.application.consumer;

import cn.ubuilding.lurker.rpc.client.Client;
import cn.ubuilding.lurker.rpc.iface.HelloService;

/**
 * @author Wu Jianfeng
 * @since 16/4/12 08:22
 */

public class BizService {

    private HelloService helloService;

    private void init() {
        helloService = new Client("127.0.0.1:2181").create(HelloService.class);
    }

}
