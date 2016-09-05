package cn.ubuilding.lurker.v2.remoting.transport.netty;

import cn.ubuilding.lurker.v2.remoting.support.AbstractServer;
import cn.ubuilding.lurker.v2.remoting.support.Server;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 15:39
 */

public class NettyServer extends AbstractServer implements Server {
    public boolean isBound() {
        return false;
    }
}
