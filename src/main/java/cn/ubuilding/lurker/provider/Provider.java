package cn.ubuilding.lurker.provider;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 16:05
 * 服务实现者对象模型
 */
@SuppressWarnings("unused")
public class Provider {

    /**
     * 唯一标识某一个RPC服务提供者
     */
    private String serviceKey;

    /**
     * RPC服务实现者
     */
    private Object implementation;

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public Object getImplementation() {
        return implementation;
    }

    public void setImplementation(Object implementation) {
        this.implementation = implementation;
    }
}
