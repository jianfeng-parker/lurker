package cn.ubuilding.lurker.application.provider.impl;


import cn.ubuilding.lurker.iface.HelloService;
import cn.ubuilding.lurker.support.annotation.Rpc;

/**
 * @author Wu Jianfeng
 * @since 16/4/12 08:54
 */

@Rpc(HelloService.class)
public class HelloServiceImpl implements HelloService {
    public String say(String name) {
        return "hello " + name;
    }
}
