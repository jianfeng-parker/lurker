package cn.ubuilding.lurker.cusumer.discover;


import cn.ubuilding.lurker.common.RemoteAddress;

/**
 * @author Wu Jianfeng
 * @since 16/4/4 21:32
 */

public abstract class Discovery {

    protected String key;

    /**
     * 发现远程服务信息
     */
    public abstract RemoteAddress discover();

    /**
     * Discovery 自身的描述信息
     */
    public abstract String description();

    /**
     * 返回 Discovery要发现的远程服务的唯一标识
     */
    public String getKey() {
        return key;
    }

}
