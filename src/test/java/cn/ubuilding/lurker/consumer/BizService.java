package cn.ubuilding.lurker.consumer;

import cn.ubuilding.lurker.biz.HelloService;
import cn.ubuilding.lurker.cusumer.Consumer;

/**
 * @author Wu Jianfeng
 * @since 16/4/12 08:22
 */

public class BizService {

    private HelloService helloService;

    private void init() {
        helloService = new Consumer("helloService", "127.0.0.1:2181", HelloService.class).instance();
    }

}
