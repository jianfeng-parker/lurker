package cn.ubuilding.lurker.application.provider.impl;


import cn.ubuilding.lurker.iface.HelloService;

/**
 * @author Wu Jianfeng
 * @since 16/4/12 08:54
 */

public class HelloServiceImpl implements HelloService {
    public String say(String name) {
        return "hello " + name;
    }
}
