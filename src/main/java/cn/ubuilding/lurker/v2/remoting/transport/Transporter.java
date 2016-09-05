package cn.ubuilding.lurker.v2.remoting.transport;

import cn.ubuilding.lurker.v2.common.URL;
import cn.ubuilding.lurker.v2.remoting.support.ChannelHandler;
import cn.ubuilding.lurker.v2.remoting.support.Client;
import cn.ubuilding.lurker.v2.remoting.support.Server;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 15:25
 */

public interface Transporter {

    Server bind(URL url, ChannelHandler listener);

    Client connect(URL url, ChannelHandler listener);
}
