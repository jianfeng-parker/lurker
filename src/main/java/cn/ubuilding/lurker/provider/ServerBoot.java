package cn.ubuilding.lurker.provider;

import cn.ubuilding.lurker.provider.registry.DefaultRegister;
import cn.ubuilding.lurker.provider.registry.Register;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 15:44
 */

public final class ServerBoot {

    /**
     * 用于启动RPC服务
     */
    private Server server;

    /**
     * 用于将服务信息发布到注册中心
     */
    private Register register;

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
    private List<ProviderInfo> providerInfos;

    /**
     * 注册逻辑执行入口
     */
    public void registry() {
        validate();// 验证参数
        try {
            if (server == null) {
                server = new Server(providerInfos, getPort());
            }
            server.start();// 启动服务
            if (null == register) {
                register = new DefaultRegister();
            }
            register.registry(host, port, providerInfos); // 发布服务
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

    public List<ProviderInfo> getProviderInfos() {
        return providerInfos;
    }

    public void setProviderInfos(List<ProviderInfo> providerInfos) {
        this.providerInfos = providerInfos;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    private void validate() {
        if (null == providerInfos || providerInfos.size() == 0) {
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
