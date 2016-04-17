package cn.ubuilding.lurker.util;

/**
 * @author Wu Jianfeng
 * @since 16/4/13 22:18
 * 封装远程服务地址信息
 */

public class HostAndPort {

    private String host;

    private int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public HostAndPort(String address) {
        if (null == address) {
            throw new NullPointerException("remote address");
        }
        String[] addr = address.split(":");
        if (addr.length != 2) {
            throw new IllegalArgumentException("invalid address, it must be formatted by 'host:port'");
        }
        this.host = addr[0];
        this.port = Integer.parseInt(addr[1]);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return host + ":" + port;
    }
}
