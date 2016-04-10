package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.provider.publish.DefaultPublisher;
import cn.ubuilding.lurker.provider.publish.Publisher;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 15:44
 */

public final class Register {

    /**
     * 用于启动RPC服务
     */
    private Server server;

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

    /**
     * 注册逻辑执行入口
     */
    public void registry() {
        validate();// 验证参数
        try {
            if (server == null) {
                server = new Server(providers, getPort());
            }
            server.start();// 启动服务
            if (null == publisher) {
                publisher = new DefaultPublisher();
            }
            publisher.publish(host, port, providers); // 发布服务
        } catch (Exception e) {
            System.out.println("registry  failure:" + e.getMessage());
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
            throw new IllegalArgumentException("not found any providers to registry");
        }
        if (this.port <= 0) {
            throw new IllegalArgumentException("invalid port:" + this.port);
        }
        if (null == this.host || this.host.trim().length() == 0) {
            throw new IllegalArgumentException("invalid host:" + this.host);
        }

    }
}
