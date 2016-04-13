package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.registry.publish.DefaultPublisher;
import cn.ubuilding.lurker.registry.publish.Publisher;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 15:44
 */

public final class Server {

    /**
     * 用于启动RPC服务
     */
    private ServerBoot serverBoot;

    /**
     * 用于将服务信息发布到注册中心
     */
    private Publisher publisher;

    /**
     * RPC服务端口
     */
    private int port;

    /**
     * RPC服务地址
     */
    private String host;

    /**
     * RPC服务是否使用SSL，默认:false
     */
    private boolean useSSL = false;

    /**
     * RPC服务提供者列表
     */
    private List<Provider> providers;

    public Server(String host, List<Provider> providers) {
        this(host, 8899, false, providers);
    }

    public Server(String host, int port, boolean useSSL, List<Provider> providers) {
        this.port = port;
        this.host = host;
        this.useSSL = useSSL;
        this.providers = providers;
    }

    /**
     * 注册逻辑执行入口
     */
    public void start() {
        validate();// 验证参数
        try {
            if (serverBoot == null) {
                serverBoot = new ServerBoot(providers, getPort());
            }
            serverBoot.start();// 启动服务
            if (null == publisher) {
                publisher = new DefaultPublisher();
            }
            publisher.publish(host, port, providers); // 发布服务
        } catch (Exception e) {
            System.out.println("publish  failure:" + e.getMessage());
        }

    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    private void validate() {
        if (null == providers || providers.size() == 0) {
            throw new IllegalArgumentException("not found any providers to publish");
        }
        if (this.port <= 0) {
            throw new IllegalArgumentException("invalid port:" + this.port);
        }
        if (null == this.host || this.host.trim().length() == 0) {
            throw new IllegalArgumentException("invalid host:" + this.host);
        }

    }
}
