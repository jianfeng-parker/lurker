package cn.ubuilding.lurker.v2.remoting.support;

import java.net.InetSocketAddress;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 15:40
 */

public abstract class AbstractServer implements Server {

    protected InetSocketAddress localAddress;

    protected InetSocketAddress bindAddress;

}
