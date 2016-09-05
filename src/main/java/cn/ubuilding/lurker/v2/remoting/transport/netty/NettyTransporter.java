package cn.ubuilding.lurker.v2.remoting.transport.netty;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.remoting.support.ChannelHandler;
import cn.ubuilding.lurker.v2.remoting.support.Client;
import cn.ubuilding.lurker.v2.remoting.support.Server;
import cn.ubuilding.lurker.v2.remoting.transport.Transporter;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 15:46
 */

public class NettyTransporter implements Transporter {

    public Server bind(URL url, ChannelHandler listener) {
        return null;
    }

    public Client connect(URL url, ChannelHandler listener) {
        return null;
    }
}
