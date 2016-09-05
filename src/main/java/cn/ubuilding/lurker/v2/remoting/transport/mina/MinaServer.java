package cn.ubuilding.lurker.v2.remoting.transport.mina;

import cn.ubuilding.lurker.v2.remoting.support.AbstractServer;

/**
 * @author Wu Jianfeng
 * @since 16/9/5 21:52
 */

public class MinaServer extends AbstractServer {
    public boolean isBound() {
        return false;
    }
}
