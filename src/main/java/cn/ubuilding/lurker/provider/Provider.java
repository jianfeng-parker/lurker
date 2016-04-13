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
    private String key;

    /**
     * 服务版本
     */
    private String version;

    /**
     * RPC服务实现者
     */
    private Object implementation;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getImplementation() {
        return implementation;
    }

    public void setImplementation(Object implementation) {
        this.implementation = implementation;
    }
}
