package cn.ubuilding.lurker.common;

/**
 * @author Wu Jianfeng
 * @since 16/4/6 14:52
 */

public class RemoteAddress {

    /**
     * 远程服务地址
     */
    private String host;

    /**
     * 远程服务端口
     */
    private int port;

    public RemoteAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
