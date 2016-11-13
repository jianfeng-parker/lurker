package cn.ubuilding.lurker.application.provider.impl;

import cn.ubuilding.lurker.support.annotation.Rpc;
import cn.ubuilding.lurker.iface.DoSomethingService;

/**
 * @author Wu Jianfeng
 * @since 16/4/16 15:09
 */
@Rpc(DoSomethingService.class)
public class DoSomethingServiceImpl implements DoSomethingService {
    public void doSomething() {
        System.out.println("do something...");
    }
}
