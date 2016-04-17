package cn.ubuilding.lurker.provider.registry;

import cn.ubuilding.lurker.provider.Provider;
import cn.ubuilding.lurker.util.HostAndPort;

import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:51
 * 服务 <strong>发布</strong>接口
 */

public abstract class Register {

    /**
     * 服务实现者地址
     */
    protected HostAndPort rpcAddress;

    /**
     * 服务实现者列表
     */
    protected List<Provider> providers;

    /**
     * 将Provider发布到注册中心
     * <p>
     * host:port
     * 服务实现者所在地址,
     * 也是需要要暴露给调用者的信息，
     * 即发布到注册中心的"远程服务地址"，
     * 客户端(服务调用方)通过serviceKey从注册中心获取到的"远程服务地址"
     */
    public abstract void publish();

    public HostAndPort getRpcAddress() {
        return rpcAddress;
    }

    public List<Provider> getProviders() {
        return providers;
    }
}
