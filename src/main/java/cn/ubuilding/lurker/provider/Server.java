package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.registry.publish.ZooKeeperPublisher;
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
     * RPC服务提供者列表
     */
    private List<Provider> providers;

    public Server(String host, List<Provider> providers, String registryAddress) {
        this(host, 8899, providers, registryAddress);
    }

    public Server(String host, int port, List<Provider> providers, String registryAddress) {
        this(host, port, providers, new ZooKeeperPublisher(host, port, providers, registryAddress));

    }

    public Server(String host, int port, List<Provider> providers, Publisher publisher) {
        this.host = host;
        this.port = port;
        this.providers = providers;
        this.publisher = publisher;
        this.serverBoot = new ServerBoot(providers, port);
    }

    /**
     * RPC服务启动入口
     */
    public void start() {
        try {
            serverBoot.start();// 启动服务
            publisher.publish(); // 发布服务
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

}
