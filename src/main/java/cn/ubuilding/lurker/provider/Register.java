package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.provider.publish.Publisher;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 15:44
 */

public class Register {

    private Server server;

    /**
     * 用于将服务信息发布到注册信息
     */
    private Publisher publisher;

    /**
     * RPC服务暴露的端口
     */
    private int port;

    private boolean ssl;

    /**
     * RPC服务提供者列表
     */
    private List<Provider> providers;

    /**
     * 注册逻辑执行入口
     */
    public void registry() {
        if (null == providers || providers.size() == 0)
            throw new RuntimeException("not found any providers to registry");
        try {
            if (server == null) {
                server = new Server(providers);
            }
            server.start();// 启动服务
            if (null != publisher) {
                publisher.publish(providers); // 发布服务
            }
        } catch (Exception e) {
            System.out.println("registry  failure:" + e.getMessage());
        }

    }

    public int getPort() {
        return port <= 0 ? 8899 : port;
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

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

}
