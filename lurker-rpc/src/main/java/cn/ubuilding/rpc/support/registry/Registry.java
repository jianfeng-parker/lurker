package cn.ubuilding.rpc.support.registry;


import java.util.List;

/**
 * @author Wu Jianfeng
 * @since 16/4/3 21:51
 * 服务 <strong>发布</strong>接口
 */

public interface Registry {

    /**
     * 注册本地开放的RPC服务
     *
     * @param serviceName    rpc服务名称
     * @param serviceAddress 服务所在地址
     */
    void register(String serviceName, String serviceAddress);

    /**
     * 根据服务名从注册中心获取服务地址
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    List<String> discover(String serviceName);

    /**
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址,格式: ip:port
     */
    void unRegister(String serviceName, String serviceAddress);

    void addListener(String serviceName, RegistryChanger listener);

    /**
     * 验证注册中心地址是否合法
     */
    void check();

    void close();

}
