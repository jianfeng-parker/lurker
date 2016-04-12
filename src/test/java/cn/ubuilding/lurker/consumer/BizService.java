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
        helloService = new Consumer("helloService", HelloService.class).instance();
    }

}
