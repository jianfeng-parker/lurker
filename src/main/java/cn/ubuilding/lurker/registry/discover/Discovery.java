package cn.ubuilding.lurker.registry.discover;


import cn.ubuilding.lurker.registry.HostAndPort;
import cn.ubuilding.lurker.registry.event.LurkerListener;

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
    public abstract HostAndPort discover();

    public abstract void stop();

    /**
     * Discovery 自身的描述信息
     */
    public abstract String description();

    /**
     * 监听serviceKey在注册中心发生的变化
     *
     * @param changer 监听器
     */
    public abstract void setChanger(LurkerListener<HostAndPort> changer);

    /**
     * 返回 Discovery要发现的远程服务的唯一标识
     */
    public String getServiceKey() {
        return serviceKey;
    }

}
