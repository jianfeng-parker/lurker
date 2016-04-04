package cn.ubuilding.lurker.cusumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 16:45
 */

public class Connection {

    private Bootstrap bootstrap;

    private InetSocketAddress address;

    private ConsumerHandler handler;

    private Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

    private long timeout = 3000;

    private boolean connected = false;

    public Connection(String host, int port) {
        this.address = new InetSocketAddress(host, port);
        this.handler = new ConsumerHandler();
    }

    private void init() {

    }
}
