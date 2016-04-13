package cn.ubuilding.lurker.registry.discover;


/**
 * @author Wu Jianfeng
 * @since 16/4/4 21:32
 * 服务 <strong>发现</strong>接口
 */

public abstract class Discovery {

    protected String serviceKey;

    /**
     * 发现远程服务信息
     */
    public abstract String discover();

    public abstract void stop();

    /**
     * Discovery 自身的描述信息
     */
    public abstract String description();

    /**
     * 返回 Discovery要发现的远程服务的唯一标识
     */
    public String getServiceKey() {
        return serviceKey;
    }

}
